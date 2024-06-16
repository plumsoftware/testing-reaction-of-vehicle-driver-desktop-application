package presentation.testmenu.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.testmenu.store.Event
import presentation.testmenu.store.Output
import presentation.testmenu.store.State

class TestMenuViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Test menu view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.TestClicked -> {
                onOutput(o = Output.TestClicked(route = event.route, testMode = event.testMode))
            }

            Event.BackCLicked -> {
                onOutput(o = Output.BackButtonClicked)
            }

            is Event.ChangeUser -> {
                state.update {
                    it.copy(
                        user = event.user
                    )
                }
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}