package tests.traffic_light_test_choice_reaction.viewmodel

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
import tests.traffic_light_test_choice_reaction.store.Effect
import tests.traffic_light_test_choice_reaction.store.Event
import tests.traffic_light_test_choice_reaction.store.State
import kotlin.random.Random

/**
 * Сложная реакция выбора: загорается случайная лампа светофора, испытуемый нажимает
 * кнопку того же цвета.
 *
 * - время реакции учитывается только для правильных нажатий;
 * - нажатие другой кнопки — «неверная реакция», попытка засчитана;
 * - нажатие до сигнала — «фальстарт», попытка не засчитывается, интервал до сигнала начинается заново;
 * - нажатие быстрее 100 мс после сигнала — упреждение («фальстарт»), попытка засчитана.
 */
class TrafficLightTestChoiceReactionViewModel(
    private val workbookStorage: WorkbookStorage,
    private val settings: Settings,
    testDTO: TestDTO,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    val state = MutableStateFlow(State(testDTO = testDTO))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }

            Event.StartTimer -> startCountdown()

            is Event.OnStimulusDrawn -> clock.onDrawn(id = event.stimulusId, nanos = event.drawNanos)

            is Event.OnLampButtonPressed -> onResponse(lampIndex = event.lampIndex, pressNanos = event.pressNanos)
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
        state.update {
            it.copy(phase = TestPhase.FOREPERIOD, currentLampIndex = -1, stimulusId = null)
        }
        phaseJob = viewModelScope.launch {
            foreperiod = Foreperiod(randomForeperiodMs(state.value.testDTO.interval))
            delay(foreperiod.durationMs)
            showStimulus()
        }
    }

    private fun showStimulus() {
        val id = clock.newStimulus()
        state.update {
            it.copy(phase = TestPhase.STIMULUS, currentLampIndex = Random.nextInt(0, 3), stimulusId = id)
        }
    }

    private fun onResponse(lampIndex: Int, pressNanos: Long) {
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
                val reactionTimeMs = clock.reactionTimeMs(pressNanos)
                state.update {
                    it.copy(
                        results = it.results.with(
                            stimulus = lampName(it.currentLampIndex),
                            foreperiodMs = foreperiod.durationMs,
                            outcome = when {
                                isAnticipation(reactionTimeMs) -> TrialOutcome.ANTICIPATION
                                lampIndex == it.currentLampIndex -> TrialOutcome.CORRECT
                                else -> TrialOutcome.WRONG
                            },
                            reactionTimeMs = reactionTimeMs
                        )
                    )
                }
                if (state.value.results.presented >= state.value.testDTO.count) finishTest() else startForeperiod()
            }

            // Во время отсчёта и на экране итогов нажатия не учитываются
            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.FINISHED -> {}
        }
    }

    private fun finishTest() {
        val result = state.value.testDTO.withResults(state.value.results)
        state.update {
            it.copy(
                phase = TestPhase.FINISHED,
                currentLampIndex = -1,
                stimulusId = null,
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
                    workbookStorage = workbookStorage
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
}
