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

class TrafficLightTestViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())
    val actions = MutableStateFlow<Action>(Action.Empty)

    init {
        println("Traffic light Test ViewModel created")
        collectActions()
    }

    private fun collectActions() {
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
                            println("${state.value.startTimerTime}")
                        }
                    }

                    Action.Empty -> {}
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {

            }
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}