package presentation.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.main.store.MainState
import presentation.tests.traffic_light_test.store.Action

class MainViewModel : ViewModel() {

    val state = MutableStateFlow(MainState())

    val trafficLightActions =
        MutableStateFlow<presentation.tests.traffic_light_test.store.Action?>(null)

    init {
        println("Main view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartTrafficLightAction -> {
                viewModelScope.launch {
                    trafficLightActions.emit(Action.StartTimer)
                }
            }

            is Event.ChangeTestDto -> {
                state.update {
                    it.copy(
                        testDTO = event.testDto
                    )
                }
            }
        }
    }
}