package tests.horn_brake_reaction.viewmodel

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
import tests.common.Foreperiod
import tests.common.HornPlayer
import tests.common.StimulusClock
import tests.common.TestPhase
import tests.common.TrialResults
import tests.common.PRACTICE_TRIALS
import tests.common.isAnticipation
import tests.common.randomForeperiodMs
import tests.common.reactionTimeMetrics
import tests.common.saveTestResult
import tests.common.withResults
import tests.horn_brake_reaction.store.Effect
import tests.horn_brake_reaction.store.Event
import tests.horn_brake_reaction.store.State

/**
 * Простая слуховая реакция: машина едет по дороге, в случайный момент звучит клаксон,
 * испытуемый нажимает педаль тормоза. Время реакции — от фактического начала звучания
 * (по позиции воспроизведения аудиовыхода) до нажатия.
 *
 * - нет торможения за [RESPONSE_WINDOW_MS] — «пропуск сигнала»;
 * - торможение до сигнала — «фальстарт», попытка не засчитывается, интервал начинается заново;
 * - нажатие быстрее 100 мс после начала звука — упреждение («фальстарт»), попытка засчитана.
 */
class HornBrakeReactionViewModel(
    testDTO: TestDTO,
    private val settings: Settings,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO, isPractice = testDTO.withPractice))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)

    private val player: HornPlayer? = try {
        HornPlayer().also { addCloseable(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        state.update { it.copy(audioError = "Звуковое устройство недоступно: ${e.message ?: e::class.simpleName}") }
        null
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartMainTest -> startMainTest()

            Event.StartInitTimer -> if (player != null) startCountdown()

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
            startForeperiod()
        }
    }

    private fun startForeperiod() {
        state.update { it.copy(phase = TestPhase.FOREPERIOD, lastTrialMissed = false) }
        phaseJob = viewModelScope.launch {
            foreperiod = Foreperiod(randomForeperiodMs(state.value.testDTO.interval))
            delay(foreperiod.durationMs)
            soundHorn()
        }
    }

    private fun soundHorn() {
        val id = clock.newStimulus()
        state.update { it.copy(phase = TestPhase.STIMULUS) }
        player?.play { onsetNanos -> clock.onDrawn(id = id, nanos = onsetNanos) }
        phaseJob = viewModelScope.launch {
            delay(RESPONSE_WINDOW_MS)
            state.update {
                it.copy(
                    phase = TestPhase.FEEDBACK,
                    lastTrialMissed = true,
                    results = it.results.with(
                        stimulus = STIMULUS_NAME,
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
                val reactionTimeMs = clock.reactionTimeMs(pressNanos)
                state.update {
                    it.copy(
                        phase = TestPhase.FEEDBACK,
                        results = it.results.with(
                            stimulus = STIMULUS_NAME,
                            foreperiodMs = foreperiod.durationMs,
                            outcome = if (isAnticipation(reactionTimeMs)) TrialOutcome.ANTICIPATION
                            else TrialOutcome.CORRECT,
                            reactionTimeMs = reactionTimeMs
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
        val audioMetrics = listOfNotNull(
            player?.let { TestMetric("audio_buffer_ms", "Буфер аудиовыхода", it.bufferMs, "мс") }
        )
        val result = state.value.testDTO.withResults(
            results = results,
            metrics = reactionTimeMetrics(results) + audioMetrics
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
        /** Окно ответа после начала звука, мс (как в тестах со светофором). */
        const val RESPONSE_WINDOW_MS = 2000L

        /** Пауза после попытки, мс. */
        const val FEEDBACK_MS = 1000L

        private const val STIMULUS_NAME = "клаксон"
    }
}
