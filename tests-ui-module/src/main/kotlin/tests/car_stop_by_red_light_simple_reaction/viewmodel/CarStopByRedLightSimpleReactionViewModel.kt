package tests.car_stop_by_red_light_simple_reaction.viewmodel

import data.model.dto.test.TestDTO
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import tests.car_stop_by_red_light_simple_reaction.store.Effect
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.store.State
import java.util.Calendar
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CarStopByRedLightSimpleReactionViewModel(
    testDTO: TestDTO,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO))
    val effect = MutableSharedFlow<Effect>()

    private lateinit var signalJob: Job

    init {
        println("Car stop by red light Test ViewModel created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartInitTimer -> {
                viewModelScope
                    .launch {
                        while (state.value.startCountDownTimer != 0) {
                            delay(1.seconds)
                            withContext(context = Dispatchers.Main) {
                                state.update {
                                    it.copy(startCountDownTimer = state.value.startCountDownTimer - 1)
                                }
                            }
                        }
                    }
                    .invokeOnCompletion {
                        animLoop()
                    }
            }

            Event.ClickOnStopPedal -> {

                if (state.value.isAnimPlaying) {
                    signalJob.cancel()

                    val end = Calendar.getInstance()
                    state.update {
                        it.copy(
                            isAnimPlaying = false,
                            end = end.timeInMillis
                        )
                    }
                    val interval: Long = state.value.end - state.value.start
                    state.value.intervals.add(interval)
                    println("Interval is $interval")

                    viewModelScope.launch(Dispatchers.Default) {
                        updateUserClickedState {
                            state.update {
                                it.copy(userClicked = state.value.userClicked + 1)
                            }
                        }
                    }
                    println("${state.value.userClicked}")
                    if (state.value.userClicked < state.value.testDTO.count) {
                        viewModelScope.launch(Dispatchers.Main) {
                            resetCarPosition()
                        }
                        animLoop()
                    } else {
                        state.update {
                            it.copy(testIsFinished = true)
                        }
                        getStdDeviation()
                        getAverage()
                    }
                }
            }

            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }
        }
    }

    private fun animLoop() {
        generateRandomInterval()
        signalJob = viewModelScope.launch(Dispatchers.Default) {
            generateSignal()
        }
    }

    private fun generateRandomInterval() {
        val intervalSignal =
            Random.nextLong(
                state.value.testDTO.interval.start,
                state.value.testDTO.interval.finish
            )

        state.update {
            it.copy(
                intervalSignal = intervalSignal,
            )
        }
    }

    private suspend fun generateSignal() {
        delay(state.value.intervalSignal)
        val start = Calendar.getInstance()
        state.update {
            it.copy(
                isAnimPlaying = true,
                start = start.timeInMillis,
            )
        }

        while (state.value.currentDistance < state.value.finalDistance) {
            withContext(Dispatchers.Main) {
                state.update {
                    it.copy(currentDistance = state.value.currentDistance + 28)
                }
            }
            delay(16.67.milliseconds)
        }

        state.value.intervals.add(0L)
        updateUserClickedState {
            state.update {
                it.copy(
                    isAnimPlaying = false,
                    errors = state.value.errors + 1,
                    userClicked = state.value.userClicked + 1
                )
            }
        }
        resetCarPosition()
        if (state.value.userClicked != state.value.testDTO.count) {
            animLoop()
        } else {
            withContext(Dispatchers.Main) {
                state.update {
                    it.copy(testIsFinished = true)
                }
            }
            getStdDeviation()
            getAverage()
        }
    }

    private fun updateUserClickedState(block: () -> Unit) {
        block.invoke()
    }

    private suspend fun resetCarPosition() {
        delay(1.seconds)
        state.update {
            it.copy(currentDistance = 0f)
        }
    }

    private fun getAverage() {
        val average = state.value.intervals.average()
        state.update {
            it.copy(testDTO = state.value.testDTO.copy(averageValue = average / 1000))
        }
    }

    private fun getStdDeviation() {
        val average = state.value.intervals.average()
        val variance = state.value.intervals.map { (it - average) * (it - average) }.average()
        state.update {
            it.copy(testDTO = state.value.testDTO.copy(stdDeviationValue = sqrt(variance)))
        }
    }
}