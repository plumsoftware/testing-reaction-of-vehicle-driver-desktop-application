package presentation.testmenu.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.tests.TrafficLight
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.testmenu.store.Effect
import presentation.testmenu.store.Event
import presentation.testmenu.store.Output
import presentation.testmenu.store.State

class TestMenuViewModel : ViewModel() {

    val state = MutableStateFlow(
        State(
            reactionTests = listOf(
                TrafficLight()
            )
        )
    )
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Test menu view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.TestClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.TestClicked(route = event.route, testMode = event.testMode))
                }
            }

            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }
        }
    }
}