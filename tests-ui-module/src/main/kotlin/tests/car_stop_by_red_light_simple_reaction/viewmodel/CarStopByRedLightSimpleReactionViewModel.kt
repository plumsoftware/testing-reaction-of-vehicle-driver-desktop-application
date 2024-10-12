package tests.car_stop_by_red_light_simple_reaction.viewmodel

import data.model.dto.test.TestDTO
import domain.storage.SessionStorage
import domain.storage.WorkbookStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import tests.car_stop_by_red_light_simple_reaction.store.Event
import tests.car_stop_by_red_light_simple_reaction.store.State
import java.util.Calendar
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CarStopByRedLightSimpleReactionViewModel(
    testDTO: TestDTO,
    private val workBookStorage: WorkbookStorage,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    val state = MutableStateFlow(State(testDTO = testDTO))

    private lateinit var signalJob: Job

    init {
        println("Car stop by red light Test ViewModel created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartInitTimer -> {
                val job = viewModelScope.launch {
                    while (state.value.startCountDownTimer != 0) {
                        delay(1.seconds)
                        withContext(context = Dispatchers.Main) {
                            state.update {
                                it.copy(startCountDownTimer = state.value.startCountDownTimer - 1)
                            }
                        }
                    }
                }
                job.invokeOnCompletion {

                }
            }

            Event.ClickOnStopPedal -> {
                val end = Calendar.getInstance()
                state.update {
                    it.copy(end = end.timeInMillis)
                }
                var interval: Long? = state.value.end - state.value.start
                state.value.intervals.add(interval!!)
                println("Interval is $interval")

                signalJob.cancel()

                state.update {
                    it.copy(userClicked = state.value.userClicked + 1)
                }

                signalJob = viewModelScope.launch {
                    delay(1.seconds)

                }
                interval = null
            }
        }
    }

    private fun generateRandomInterval() {
        val intervalSignal =
            Random.nextLong(
                state.value.testDTO.interval.start,
                state.value.testDTO.interval.finish
            )

        state.update {
            it.copy(intervalSignal = intervalSignal)
        }

        println("==================")
        println("Count of flickering is $intervalSignal")
    }
}