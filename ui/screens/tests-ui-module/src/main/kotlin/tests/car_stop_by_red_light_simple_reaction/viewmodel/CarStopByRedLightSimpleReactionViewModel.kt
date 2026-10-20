package tests.car_stop_by_red_light_simple_reaction.viewmodel

import data.model.dto.test.TestDTO
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
import tests.car_stop_by_red_light_simple_reaction.store.Effect
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.store.State
import data.model.dto.test.TrialOutcome
import tests.common.COUNTDOWN_SECONDS
import tests.common.Foreperiod
import tests.common.lampName
import tests.common.StimulusClock
import tests.common.TestPhase
import tests.common.isAnticipation
import tests.common.randomForeperiodMs
import tests.common.saveTestResult
import tests.common.withResults

/**
 * Простая реакция: машина едет, светофор впереди горит зелёным; в случайный момент загорается
 * красный, испытуемый нажимает педаль тормоза. Время реакции — от первой отрисовки красного до нажатия.
 *
 * - нет торможения за [RESPONSE_WINDOW_MS] после красного — «пропуск сигнала» (проезд на красный);
 * - торможение на зелёный — «фальстарт», попытка не засчитывается, интервал до сигнала начинается заново;
 * - нажатие быстрее 100 мс после красного — упреждение («фальстарт»), попытка засчитана.
 */
class CarStopByRedLightSimpleReactionViewModel(
    testDTO: TestDTO,
    private val settings: Settings,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)

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
            startForeperiod()
        }
    }

    private fun startForeperiod() {
        state.update { it.copy(phase = TestPhase.FOREPERIOD, stimulusId = null, lastTrialMissed = false) }
        phaseJob = viewModelScope.launch {
            foreperiod = Foreperiod(randomForeperiodMs(state.value.testDTO.interval))
            delay(foreperiod.durationMs)
            showRedLight()
        }
    }

    private fun showRedLight() {
        val id = clock.newStimulus()
        state.update { it.copy(phase = TestPhase.STIMULUS, stimulusId = id) }
        phaseJob = viewModelScope.launch {
            delay(RESPONSE_WINDOW_MS)
            // Тормоз так и не нажат — проезд на красный
            state.update {
                it.copy(
                    phase = TestPhase.FEEDBACK,
                    lastTrialMissed = true,
                    results = it.results.with(
                        stimulus = lampName(RED_LAMP_INDEX),
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
                            stimulus = lampName(RED_LAMP_INDEX),
                            foreperiodMs = foreperiod.durationMs,
                            outcome = if (isAnticipation(reactionTimeMs)) TrialOutcome.ANTICIPATION
                            else TrialOutcome.CORRECT,
                            reactionTimeMs = reactionTimeMs
                        )
                    )
                }
                phaseJob = viewModelScope.launch { pauseThenNextTrial() }
            }

            // Во время отсчёта, паузы после попытки и на экране итогов нажатия не учитываются
            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.FINISHED -> {}
        }
    }

    /** Показывает остановившуюся (или проехавшую на красный) машину, затем следующая попытка. */
    private suspend fun pauseThenNextTrial() {
        delay(FEEDBACK_MS)
        if (state.value.results.presented >= state.value.testDTO.count) finishTest() else startForeperiod()
    }

    private fun finishTest() {
        val result = state.value.testDTO.withResults(state.value.results)
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

    companion object {
        /** Окно ответа после загорания красного, мс (как в тесте реакции различения). */
        const val RESPONSE_WINDOW_MS = 2000L

        private const val RED_LAMP_INDEX = 0

        /** Пауза после попытки, мс. */
        const val FEEDBACK_MS = 1000L
    }
}
