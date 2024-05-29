package presentation.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MainViewModel : ViewModel() {

    val trafficLightActions = MutableStateFlow<presentation.tests.traffic_light_test.store.Action?>(null)

    init {
        println("Main view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.StartTrafficLightAction -> {
                viewModelScope.launch {
                    trafficLightActions.emit(presentation.tests.traffic_light_test.store.Action.StartTimer)
                }
            }
        }
    }
}