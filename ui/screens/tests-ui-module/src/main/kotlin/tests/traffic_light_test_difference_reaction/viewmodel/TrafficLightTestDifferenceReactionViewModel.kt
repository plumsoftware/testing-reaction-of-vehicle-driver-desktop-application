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
import data.model.dto.test.TrialOutcome
import tests.common.COUNTDOWN_SECONDS
import tests.common.Foreperiod
import tests.common.goNoGoMetrics
import tests.common.lampName
import tests.common.reactionTimeMetrics
import tests.common.StimulusClock
import tests.common.TestPhase
import tests.common.TrialResults
import tests.common.PRACTICE_TRIALS
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

    val state = MutableStateFlow(State(testDTO = testDTO, isPractice = testDTO.withPractice))
    val effect = MutableSharedFlow<Effect>()

    private val clock = StimulusClock()
    private var phaseJob: Job? = null
    private var foreperiod = Foreperiod(durationMs = 0)

    fun onEvent(event: Event) {
        when (event) {
            Event.StartMainTest -> startMainTest()

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
        // Окно ответа: по его истечении попытка завершается без нажатия
        phaseJob = viewModelScope.launch {
            delay(RESPONSE_WINDOW_MS)
            state.update {
                it.copy(
                    results = it.results.with(
                        stimulus = lampName(it.currentLampIndex),
                        foreperiodMs = foreperiod.durationMs,
                        outcome = if (it.currentLampIndex == GO_LAMP_INDEX) TrialOutcome.OMISSION
                        else TrialOutcome.CORRECT_REJECTION
                    )
                )
            }
            nextTrialOrFinish()
        }
    }

    private fun onResponse(pressNanos: Long) {
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
                        results = it.results.with(
                            stimulus = lampName(it.currentLampIndex),
                            foreperiodMs = foreperiod.durationMs,
                            outcome = when {
                                isAnticipation(reactionTimeMs) -> TrialOutcome.ANTICIPATION
                                it.currentLampIndex == GO_LAMP_INDEX -> TrialOutcome.CORRECT
                                else -> TrialOutcome.WRONG
                            },
                            reactionTimeMs = reactionTimeMs
                        )
                    )
                }
                nextTrialOrFinish()
            }

            // Во время отсчёта и на экране итогов нажатия не учитываются
            TestPhase.COUNTDOWN, TestPhase.FEEDBACK, TestPhase.PRACTICE_FINISHED, TestPhase.FINISHED -> {}
        }
    }

    private fun nextTrialOrFinish() {
        if (state.value.results.presented >= trialsTarget()) finishTest() else startForeperiod()
    }

    private fun finishTest() {
        if (state.value.isPractice) {
            finishPractice()
            return
        }
        val results = state.value.results
        val result = state.value.testDTO.withResults(
            results = results,
            metrics = reactionTimeMetrics(results) + goNoGoMetrics(results, goStimulus = lampName(GO_LAMP_INDEX))
        )
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

    /** Сколько попыток в текущей части: тренировочной или зачётной. */
    private fun trialsTarget(): Int = if (state.value.isPractice) PRACTICE_TRIALS else state.value.testDTO.count

    /** Тренировочные попытки отбрасываются, ждём, пока испытуемый начнёт зачётную часть. */
    private fun finishPractice() {
        phaseJob?.cancel()
        state.update {
            it.copy(
                phase = TestPhase.PRACTICE_FINISHED,
                stimulusId = null,
                currentLampIndex = -1,
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
        /** Лампа, на которую нужно реагировать: зелёная. */
        const val GO_LAMP_INDEX = 2

        /** Окно ответа после сигнала, мс (как в исходной версии — 2 с). */
        const val RESPONSE_WINDOW_MS = 2000L
    }
}
