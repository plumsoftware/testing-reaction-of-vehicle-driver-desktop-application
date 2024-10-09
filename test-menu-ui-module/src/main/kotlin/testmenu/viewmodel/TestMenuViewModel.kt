package testmenu.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.tests.TrafficLightChoiceReaction
import model.tests.TrafficLightDifferenceReaction
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import testmenu.store.Effect
import testmenu.store.Event
import testmenu.store.State

class TestMenuViewModel : ViewModel() {

    val state = MutableStateFlow(
        State(
            reactionTests = listOf(
                TrafficLightChoiceReaction(),
                TrafficLightDifferenceReaction()
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
                    effect.emit(Effect.TestClicked(route = event.route, testDTO = event.testDTO))
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