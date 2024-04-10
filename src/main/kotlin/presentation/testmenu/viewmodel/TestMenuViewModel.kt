package presentation.testmenu.viewmodel

import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.testmenu.store.Event
import presentation.testmenu.store.Output

class TestMenuViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {
    fun onEvent(event: Event) {
        when (event) {
            Event.Test1CLicked -> {
                onOutput(o = Output.Test1Clicked)
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