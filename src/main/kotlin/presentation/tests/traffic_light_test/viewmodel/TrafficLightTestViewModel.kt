package presentation.tests.traffic_light_test.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.tests.traffic_light_test.store.Action
import presentation.tests.traffic_light_test.store.Event
import presentation.tests.traffic_light_test.store.Output
import presentation.tests.traffic_light_test.store.State
import java.util.Calendar
import kotlin.random.Random

class TrafficLightTestViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())
    val actions = MutableStateFlow<Action>(Action.StartTimer)

    init {
        println("Traffic light Test ViewModel created")
    }

    fun collectActions() {
        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    Action.StartTimer -> {
                        while (state.value.startTimerTime > 0) {
                            delay(1000)
                            state.update {
                                it.copy(
                                    startTimerTime = state.value.startTimerTime - 1
                                )
                            }
                        }
                        if (state.value.startTimerTime == 0) {
                            actions.emit(Action.GenerateRandomSignal)
                        }
                    }

                    Action.GenerateRandomSignal -> {
                        generateRandomSignal()
                    }
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                onOutput(Output.BackButtonClicked)
            }

            is Event.OnTrafficLightLampButtonClicked -> {
                generateRandomInterval()
            }

            Event.RegisterTrafficLightLampButtonClick -> {
                val end: Calendar = Calendar.getInstance()
                end.timeInMillis = System.currentTimeMillis()
                state.update {
                    it.copy(
                        end = end.timeInMillis
                    )
                }
                val interval = state.value.end - state.value.start
                registerData(interval, state.value.start, state.value.end)
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }

    private fun generateRandomSignal() {
        if (state.value.startTimerTime == 0) {
            val currentIndex = Random.nextInt(0, 3)

            state.update {
                it.copy(
                    currentLampIndex = currentIndex
                )
            }
        }
    }

    private fun generateRandomInterval() = viewModelScope.launch {
        if (state.value.startTimerTime == 0 && state.value.currentLampIndex != -1) {
            state.update {
                it.copy(
                    currentLampIndex = -1
                )
            }
            val intervalSignal = Random.nextLong(2000, 10000)
            delay(intervalSignal)
            generateRandomSignal()
        }
    }

    private fun registerData(intervalSignal: Long, start: Long, end: Long) {
        println("==================")
        val instance: Calendar = Calendar.getInstance()
        instance.timeInMillis = intervalSignal
        println("Интервал сигнала: ${instance.get(Calendar.HOUR_OF_DAY)}:${instance.get(Calendar.MINUTE)}:${instance.get(Calendar.SECOND)}")
        instance.timeInMillis = start
        println("Сигнал показался: ${instance.get(Calendar.HOUR_OF_DAY)}:${instance.get(Calendar.MINUTE)}:${instance.get(Calendar.SECOND)}")
        instance.timeInMillis = end
        println("На кнопку нажали: ${instance.get(Calendar.HOUR_OF_DAY)}:${instance.get(Calendar.MINUTE)}:${instance.get(Calendar.SECOND)}")
        println("==================")
    }
}