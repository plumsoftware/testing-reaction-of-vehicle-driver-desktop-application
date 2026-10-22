package tests.rmo_stop_line.viewmodel

import data.model.dto.test.ReactionStatistics
import data.model.dto.test.TestDTO
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialOutcome
import data.model.regular.settings.Settings
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import other.components.SaveStatus
import tests.common.COUNTDOWN_SECONDS
import tests.common.markPartStarted
import tests.common.Foreperiod
import tests.common.TestPhase
import tests.common.PRACTICE_TRIALS
import tests.common.TrialResults
import tests.common.randomForeperiodMs
import tests.common.saveTestResult
import tests.common.withResults
import tests.rmo_stop_line.store.Effect
import tests.rmo_stop_line.store.Event
import tests.rmo_stop_line.store.State
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Реакция на движущийся объект (РДО) в дорожном варианте: машина с постоянной скоростью
 * подъезжает к стоп-линии у красного светофора, испытуемый должен нажать тормоз точно
 * в момент, когда передний бампер окажется на линии.
 *
 * Измеряется ошибка = момент нажатия − момент пересечения линии, мс: отрицательная —
 * опережение (преобладание возбуждения), положительная — запаздывание (преобладание торможения).
 * Время движения до линии на каждой попытке случайно выбирается из [TRAVEL_TIMES_MS], чтобы
 * нельзя было заучить ритм. Перед началом движения — случайная пауза из настроек теста.
 *
 * - нажатие, пока машина стоит, — «фальстарт», попытка не засчитывается, пауза начинается заново;
 * - нет нажатия до [LATE_LIMIT_MS] после пересечения линии — «пропуск сигнала».
 */
class RmoStopLineViewModel(
    testDTO: TestDTO,
    private val settings: Settings,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO, isPractice = testDTO.withPractice))
    val effect = MutableSharedFlow<Effect>()

    private var phaseJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)

    fun onEvent(event: Event) {
        when (event) {
            Event.StartMainTest -> startMainTest()

            Event.StartInitTimer -> startCountdown()

            is Event.OnBrakePressed -> onBrake(pressNanos = event.pressNanos)

            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }
        }
    }

    private fun startCountdown() {
        if (phaseJob != null) return
        phaseJob = viewModelScope.launch {
            for (second in COUNTDOWN_SECONDS downTo 1) {
                state.update { it.copy(countdown = second) }
                delay(1000)
            }
            state.update { it.copy(testDTO = it.testDTO.markPartStarted(isPractice = it.isPractice)) }
            startForeperiod()
        }
    }

    private fun startForeperiod() {
        state.update { it.copy(phase = TestPhase.FOREPERIOD, motionStartNanos = null, stoppedProgress = null) }
        phaseJob = viewModelScope.launch {
            foreperiod = Foreperiod(randomForeperiodMs(state.value.testDTO.interval))
            delay(foreperiod.durationMs)
            startMotion()
        }
    }

    private fun startMotion() {
        val travelTimeMs = TRAVEL_TIMES_MS[Random.nextInt(TRAVEL_TIMES_MS.size)]
        state.update {
            it.copy(
                phase = TestPhase.STIMULUS,
                travelTimeMs = travelTimeMs,
                motionStartNanos = System.nanoTime()
            )
        }
        phaseJob = viewModelScope.launch {
            delay(travelTimeMs + LATE_LIMIT_MS)
            state.update {
                it.copy(
                    phase = TestPhase.FEEDBACK,
                    results = it.results.with(
                        stimulus = stimulusName(travelTimeMs),
                        foreperiodMs = foreperiod.durationMs,
                        outcome = TrialOutcome.OMISSION
                    )
                )
            }
            pauseThenNextTrial()
        }
    }

    private fun onBrake(pressNanos: Long) {
        when (state.value.phase) {
            TestPhase.FOREPERIOD -> {
                phaseJob?.cancel()
                state.update {
                    it.copy(
                        results = it.results.with(
                            stimulus = null,
                            foreperiodMs = foreperiod.durationMs,
                            outcome = TrialOutcome.FALSE_START,
                            reactionTimeMs = foreperiod.msBeforeSignal(pressNanos)
                        )
                    )
                }
                startForeperiod()
            }

            TestPhase.STIMULUS -> {
                phaseJob?.cancel()
                val motionStart = state.value.motionStartNanos ?: return
                val travelTimeMs = state.value.travelTimeMs
                val elapsedMs = (pressNanos - motionStart) / 1_000_000.0
                val errorMs = elapsedMs - travelTimeMs
                state.update {
                    it.copy(
                        phase = TestPhase.FEEDBACK,
                        stoppedProgress = (elapsedMs / travelTimeMs).toFloat(),
                        results = it.results.with(
                            stimulus = stimulusName(travelTimeMs),
                            foreperiodMs = foreperiod.durationMs,
                            outcome = TrialOutcome.CORRECT,
                            reactionTimeMs = errorMs
                        )
                    )
                }
                phaseJob = viewModelScope.launch { pauseThenNextTrial() }
            }

            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.PRACTICE_FINISHED, TestPhase.FINISHED -> {}
        }
    }

    private suspend fun pauseThenNextTrial() {
        delay(FEEDBACK_MS)
        if (state.value.results.presented >= trialsTarget()) finishTest() else startForeperiod()
    }

    private fun finishTest() {
        if (state.value.isPractice) {
            finishPractice()
            return
        }
        val results = state.value.results
        val errors = results.reactionTimesMs
        // Для РДО «среднее» в БД — средняя абсолютная ошибка, «SD» — SD ошибки со знаком
        val result = state.value.testDTO
            .withResults(results = results, metrics = rmoMetrics(results))
            .copy(
                averageValue = if (errors.isEmpty()) 0.0 else errors.map { abs(it) }.average() / 1000,
                stdDeviationValue = ReactionStatistics.sampleStdDeviationSeconds(errors)
            )
        state.update {
            it.copy(phase = TestPhase.FINISHED, testDTO = result, saveStatus = SaveStatus.SAVING)
        }
        viewModelScope.launch {
            val status = try {
                val saved = saveTestResult(
                    result = result,
                    settings = settings,
                    sessionStorage = sessionStorage,
                    workbookStorage = workBookStorage
                )
                state.update { it.copy(testDTO = saved) }
                SaveStatus.SAVED
            } catch (e: Exception) {
                e.printStackTrace()
                SaveStatus.FAILED
            }
            state.update { it.copy(saveStatus = status) }
        }
    }

    private fun rmoMetrics(results: TrialResults): List<TestMetric> {
        val stopped = results.trials.filter { it.outcome == TrialOutcome.CORRECT && it.reactionTimeMs != null }
        val errors = stopped.map { it.reactionTimeMs!! }
        if (errors.isEmpty()) return emptyList()

        val early = errors.count { it < -EXACT_TOLERANCE_MS }
        val late = errors.count { it > EXACT_TOLERANCE_MS }
        val exact = errors.size - early - late

        return buildList {
            add(TestMetric("rmo_mean_signed_error_ms", "Средняя ошибка со знаком (− опережение, + запаздывание)", errors.average(), "мс"))
            add(TestMetric("rmo_mean_abs_error_ms", "Средняя абсолютная ошибка", errors.map { abs(it) }.average(), "мс"))
            ReactionStatistics.median(errors.map { abs(it) })?.let {
                add(TestMetric("rmo_median_abs_error_ms", "Медиана абсолютной ошибки", it, "мс"))
            }
            if (errors.size >= 2) {
                val mean = errors.average()
                val sd = sqrt(errors.sumOf { (it - mean) * (it - mean) } / (errors.size - 1))
                add(TestMetric("rmo_sd_error_ms", "Стандартное отклонение ошибки (выборочное)", sd, "мс"))
            }
            add(TestMetric("rmo_early_count", "Опережения (раньше линии более чем на $EXACT_TOLERANCE_MS мс)", early.toDouble(), "шт."))
            add(TestMetric("rmo_exact_count", "Точные остановки (±$EXACT_TOLERANCE_MS мс)", exact.toDouble(), "шт."))
            add(TestMetric("rmo_late_count", "Запаздывания (позже линии более чем на $EXACT_TOLERANCE_MS мс)", late.toDouble(), "шт."))
            if (early + late > 0) {
                add(
                    TestMetric(
                        "rmo_balance_index",
                        "Индекс баланса (з − о) / (з + о): > 0 — преобладание торможения, < 0 — возбуждения",
                        (late - early).toDouble() / (late + early),
                        ""
                    )
                )
            }
            // Средняя абсолютная ошибка отдельно для каждой скорости
            TRAVEL_TIMES_MS.forEach { travelTime ->
                val forSpeed = stopped.filter { it.stimulus == stimulusName(travelTime) }.map { abs(it.reactionTimeMs!!) }
                if (forSpeed.isNotEmpty()) {
                    add(
                        TestMetric(
                            "rmo_mean_abs_error_${travelTime}ms",
                            "Средняя абсолютная ошибка, ${stimulusName(travelTime)}",
                            forSpeed.average(),
                            "мс"
                        )
                    )
                }
            }
        }
    }

    /** Сколько попыток в текущей части: тренировочной или зачётной. */
    private fun trialsTarget(): Int = if (state.value.isPractice) PRACTICE_TRIALS else state.value.testDTO.count

    /** Тренировочные попытки отбрасываются, ждём, пока испытуемый начнёт зачётную часть. */
    private fun finishPractice() {
        phaseJob?.cancel()
        state.update {
            it.copy(
                phase = TestPhase.PRACTICE_FINISHED,
                results = TrialResults()
            )
        }
    }

    private fun startMainTest() {
        if (state.value.phase != TestPhase.PRACTICE_FINISHED) return
        state.update { it.copy(isPractice = false, phase = TestPhase.COUNTDOWN, countdown = COUNTDOWN_SECONDS) }
        phaseJob = null
        startCountdown()
    }

    companion object {
        /** Возможные времена движения от старта до стоп-линии (три скорости), мс. */
        val TRAVEL_TIMES_MS = listOf(1500L, 2000L, 2500L)

        /** Сколько ждать нажатия после пересечения линии, прежде чем засчитать пропуск, мс. */
        const val LATE_LIMIT_MS = 1000L

        /** Допуск «точной» остановки, мс (≈ 1 кадр при 50–60 Гц). */
        const val EXACT_TOLERANCE_MS = 20.0

        /** Пауза после попытки (видно, где остановилась машина), мс. */
        const val FEEDBACK_MS = 1500L

        fun stimulusName(travelTimeMs: Long): String =
            String.format(Locale.ROOT, "до линии %.1f с", travelTimeMs / 1000.0).replace('.', ',')
    }
}
