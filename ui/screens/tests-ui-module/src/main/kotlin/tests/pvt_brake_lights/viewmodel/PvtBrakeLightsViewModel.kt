package tests.pvt_brake_lights.viewmodel

import data.model.dto.test.ReactionStatistics
import data.model.dto.test.TestDTO
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialOutcome
import data.model.regular.settings.Settings
import data.model.regular.user.Interval
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
import tests.common.Foreperiod
import tests.common.StimulusClock
import tests.common.TestPhase
import tests.common.TrialResults
import tests.common.isAnticipation
import tests.common.randomForeperiodMs
import tests.common.saveTestResult
import tests.common.withResults
import tests.pvt_brake_lights.store.Effect
import tests.pvt_brake_lights.store.Event
import tests.pvt_brake_lights.store.State
import java.util.Locale

/**
 * Тест длительной бдительности (Psychomotor Vigilance Task, PVT) в дорожном варианте:
 * испытуемый долго едет за впереди идущей машиной; в случайный момент у неё загораются
 * стоп-сигналы, нужно нажать педаль тормоза.
 *
 * Параметры по стандартному PVT (Dinges & Powell, 1985; Basner & Dinges, 2011):
 * - длительность [durationMinutes] (эталон — 10 мин), тест завершается по времени;
 * - интервал между ответом и следующим сигналом — равномерно 2–10 с;
 * - сигнал горит до ответа; после ответа 1 с показывается время реакции;
 * - нажатие без сигнала или быстрее 100 мс после него — фальстарт; после фальстарта интервал
 *   начинается заново;
 * - реакция ≥ 500 мс — провал внимания (lapse); нет реакции 30 с — тайм-аут, тоже провал.
 *
 * Отличие от лабораторного PVT — только вид стимула (стоп-сигналы вместо счётчика миллисекунд).
 */
class PvtBrakeLightsViewModel(
    testDTO: TestDTO,
    private val durationMinutes: Int,
    private val settings: Settings,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO.copy(interval = PVT_INTERVAL)))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null
    private var feedbackJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)
    private var testStartNanos = 0L

    private val durationNanos = durationMinutes * 60_000_000_000L

    fun onEvent(event: Event) {
        when (event) {
            Event.StartInitTimer -> startCountdown()

            is Event.OnStimulusDrawn -> clock.onDrawn(id = event.stimulusId, nanos = event.drawNanos)

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
            testStartNanos = System.nanoTime()
            startInterval()
        }
    }

    private fun isTimeOver(): Boolean = System.nanoTime() - testStartNanos >= durationNanos

    /** Интервал до следующего сигнала; если время теста вышло — сигнал не предъявляется. */
    private fun startInterval() {
        if (isTimeOver()) {
            finishTest()
            return
        }
        state.update { it.copy(phase = TestPhase.FOREPERIOD, stimulusId = null) }
        phaseJob = viewModelScope.launch {
            foreperiod = Foreperiod(randomForeperiodMs(PVT_INTERVAL))
            delay(foreperiod.durationMs)
            if (isTimeOver()) finishTest() else showBrakeLights()
        }
    }

    private fun showBrakeLights() {
        val id = clock.newStimulus()
        state.update { it.copy(phase = TestPhase.STIMULUS, stimulusId = id) }
        phaseJob = viewModelScope.launch {
            delay(TIMEOUT_MS)
            state.update {
                it.copy(
                    results = it.results.with(
                        stimulus = STIMULUS_NAME,
                        foreperiodMs = foreperiod.durationMs,
                        outcome = TrialOutcome.OMISSION
                    )
                )
            }
            showFeedback("Нет реакции")
            startInterval()
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
                showFeedback("Фальстарт")
                startInterval()
            }

            TestPhase.STIMULUS -> {
                phaseJob?.cancel()
                val reactionTimeMs = clock.reactionTimeMs(pressNanos)
                val anticipation = isAnticipation(reactionTimeMs)
                state.update {
                    it.copy(
                        results = it.results.with(
                            stimulus = STIMULUS_NAME,
                            foreperiodMs = foreperiod.durationMs,
                            outcome = if (anticipation) TrialOutcome.ANTICIPATION else TrialOutcome.CORRECT,
                            reactionTimeMs = reactionTimeMs
                        )
                    )
                }
                showFeedback(
                    if (anticipation) "Фальстарт" else String.format(Locale.ROOT, "%.0f мс", reactionTimeMs)
                )
                startInterval()
            }

            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.FINISHED -> {}
        }
    }

    /** Сообщение на 1 с; интервал до следующего сигнала при этом уже идёт (как в стандартном PVT). */
    private fun showFeedback(text: String) {
        feedbackJob?.cancel()
        state.update { it.copy(feedbackText = text) }
        feedbackJob = viewModelScope.launch {
            delay(FEEDBACK_MS)
            state.update { it.copy(feedbackText = null) }
        }
    }

    private fun finishTest() {
        phaseJob?.cancel()
        feedbackJob?.cancel()
        val results = state.value.results
        val result = state.value.testDTO
            .copy(count = results.presented)
            .withResults(results = results, metrics = pvtMetrics(results))
        state.update {
            it.copy(
                phase = TestPhase.FINISHED,
                stimulusId = null,
                feedbackText = null,
                testDTO = result,
                saveStatus = SaveStatus.SAVING
            )
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

    /** Стандартные показатели PVT (Basner & Dinges, 2011). */
    private fun pvtMetrics(results: TrialResults): List<TestMetric> {
        val rts = results.reactionTimesMs
        val speeds = rts.map { 1000.0 / it }
        val lapses = rts.count { it >= LAPSE_THRESHOLD_MS } + results.omissions
        val falseStarts = results.prematureResponses
        val stimuli = results.presented
        return buildList {
            add(TestMetric("pvt_duration_min", "Длительность теста", durationMinutes.toDouble(), "мин"))
            add(TestMetric("pvt_lapses", "Провалы внимания (RT ≥ 500 мс и тайм-ауты)", lapses.toDouble(), "шт."))
            add(TestMetric("pvt_false_starts", "Фальстарты", falseStarts.toDouble(), "шт."))
            if (stimuli + falseStarts > 0) {
                add(
                    TestMetric(
                        "pvt_performance_percent",
                        "Показатель выполнения: 1 − (провалы + фальстарты) / (сигналы + фальстарты)",
                        100.0 * (1 - (lapses + falseStarts).toDouble() / (stimuli + falseStarts)),
                        "%"
                    )
                )
            }
            ReactionStatistics.median(rts)?.let { add(TestMetric("rt_median_ms", "Медиана времени реакции", it, "мс")) }
            if (speeds.isNotEmpty()) {
                add(TestMetric("pvt_mean_speed", "Средняя скорость реакции 1/RT", speeds.average(), "1/с"))
            }
            ReactionStatistics.meanOfFastest(rts)?.let {
                add(TestMetric("rt_fastest10_ms", "Среднее 10 % самых быстрых реакций", it, "мс"))
            }
            // Самые медленные 10 % по скорости = наименьшие значения 1/RT
            ReactionStatistics.meanOfFastest(speeds.map { -it })?.let {
                add(TestMetric("pvt_slowest10_speed", "Средняя скорость 10 % самых медленных реакций", -it, "1/с"))
            }
        }
    }

    companion object {
        /** Интервал до сигнала в стандартном PVT, мс. */
        val PVT_INTERVAL = Interval(start = 2000, finish = 10000)

        /** Порог провала внимания, мс. */
        const val LAPSE_THRESHOLD_MS = 500.0

        /** Тайм-аут без реакции, мс. */
        const val TIMEOUT_MS = 30_000L

        /** Длительность показа времени реакции после ответа, мс. */
        const val FEEDBACK_MS = 1000L

        private const val STIMULUS_NAME = "стоп-сигналы"
    }
}
