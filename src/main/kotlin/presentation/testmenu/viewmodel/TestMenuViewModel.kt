package presentation.testmenu.viewmodel

import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.testmenu.store.Event
import presentation.testmenu.store.Output

class TestMenuViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    init {
        println("Test menu view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.TestClicked -> {
                onOutput(o = Output.TestClicked(route = event.route))
            }

            Event.BackCLicked -> {
                onOutput(o = Output.BackButtonClicked)
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}