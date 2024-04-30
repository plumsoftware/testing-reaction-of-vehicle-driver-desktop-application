package presentation.tests.traffic_light_test.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()

                state.update {
                    it.copy(
                        end = calendar.timeInMillis
                    )
                }

                println("==================")
                println(
                    "Button was clicked: ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                        calendar.get(
                            Calendar.SECOND
                        )
                    }"
                )

                generateRandomInterval()
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }

    private fun generateRandomSignal() {
        if (state.value.startTimerTime == 0) {
            val currentIndex = Random.nextInt(0, 3)

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()

            state.update {
                it.copy(
                    currentLampIndex = currentIndex,
                    start = calendar.timeInMillis
                )
            }
            println("==================")
            println(
                "Signal was shown: ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                    calendar.get(
                        Calendar.SECOND
                    )
                }"
            )
        }
    }

    private fun generateRandomInterval() {
        viewModelScope.launch {
            if (state.value.startTimerTime == 0 && state.value.currentLampIndex != -1) {
                state.update {
                    it.copy(
                        currentLampIndex = -1
                    )
                }
                val intervalSignal = Random.nextLong(2000, 10000)
                println("==================")
                println("Signal interval: ${intervalSignal}msc")
                delay(intervalSignal)
                generateRandomSignal()
            }
        }
    }
}