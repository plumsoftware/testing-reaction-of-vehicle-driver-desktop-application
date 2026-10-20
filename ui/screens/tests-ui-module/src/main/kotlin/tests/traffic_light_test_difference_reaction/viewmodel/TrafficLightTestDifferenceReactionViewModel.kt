package tests.traffic_light_test_difference_reaction.viewmodel

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
import tests.common.COUNTDOWN_SECONDS
import tests.common.StimulusClock
import tests.common.TestPhase
import tests.common.isAnticipation
import tests.common.randomForeperiodMs
import tests.common.saveTestResult
import tests.common.withResults
import tests.traffic_light_test_difference_reaction.store.Effect
import tests.traffic_light_test_difference_reaction.store.Event
import tests.traffic_light_test_difference_reaction.store.State
import kotlin.random.Random

/**
 * Реакция различения (go/no-go): загорается случайная лампа, испытуемый нажимает педаль газа
 * только на зелёный и не реагирует на красный и жёлтый.
 *
 * - время реакции учитывается только для нажатий на зелёный;
 * - нажатие на красный/жёлтый — «неверная реакция»;
 * - нет нажатия на зелёный за [RESPONSE_WINDOW_MS] — «пропуск сигнала»;
 * - нет нажатия на красный/жёлтый за [RESPONSE_WINDOW_MS] — правильное торможение, ошибки нет;
 * - нажатие до сигнала — «фальстарт», попытка не засчитывается, интервал до сигнала начинается заново;
 * - нажатие быстрее 100 мс после сигнала — упреждение («фальстарт»), попытка засчитана.
 */
class TrafficLightTestDifferenceReactionViewModel(
    private val workbookStorage: WorkbookStorage,
    private val settings: Settings,
    testDTO: TestDTO,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    val state = MutableStateFlow(State(testDTO = testDTO))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }

            Event.StartTimer -> startCountdown()

            is Event.OnStimulusDrawn -> clock.onDrawn(id = event.stimulusId, nanos = event.drawNanos)

            is Event.OnGasPedalPressed -> onResponse(pressNanos = event.pressNanos)
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
            delay(randomForeperiodMs(state.value.testDTO.interval))
            showStimulus()
        }
    }

    private fun showStimulus() {
        val id = clock.newStimulus()
        state.update {
            it.copy(phase = TestPhase.STIMULUS, currentLampIndex = Random.nextInt(0, 3), stimulusId = id)
        }
        // Окно ответа: по его истечении попытка завершается без нажатия
        phaseJob = viewModelScope.launch {
            delay(RESPONSE_WINDOW_MS)
            state.update {
                it.copy(
                    results = if (it.currentLampIndex == GO_LAMP_INDEX) it.results.withOmission()
                    else it.results.withCorrectRejection()
                )
            }
            nextTrialOrFinish()
        }
    }

    private fun onResponse(pressNanos: Long) {
        when (state.value.phase) {
            TestPhase.FOREPERIOD -> {
                phaseJob?.cancel()
                state.update { it.copy(results = it.results.withFalseStart()) }
                startForeperiod()
            }

            TestPhase.STIMULUS -> {
                phaseJob?.cancel()
                val reactionTimeMs = clock.reactionTimeMs(pressNanos)
                state.update {
                    it.copy(
                        results = when {
                            isAnticipation(reactionTimeMs) -> it.results.withAnticipation()
                            it.currentLampIndex == GO_LAMP_INDEX -> it.results.withCorrect(reactionTimeMs!!)
                            else -> it.results.withWrong()
                        }
                    )
                }
                nextTrialOrFinish()
            }

            // Во время отсчёта и на экране итогов нажатия не учитываются
            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.FINISHED -> {}
        }
    }

    private fun nextTrialOrFinish() {
        if (state.value.results.presented >= state.value.testDTO.count) finishTest() else startForeperiod()
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

    companion object {
        /** Лампа, на которую нужно реагировать: зелёная. */
        const val GO_LAMP_INDEX = 2

        /** Окно ответа после сигнала, мс (как в исходной версии — 2 с). */
        const val RESPONSE_WINDOW_MS = 2000L
    }
}
