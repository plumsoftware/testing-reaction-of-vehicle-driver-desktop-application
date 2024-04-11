package presentation.home.viewmodel

import androidx.compose.runtime.Immutable
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.home.store.Event
import presentation.home.store.Output

@Immutable
class HomeViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    init {
        println("Home view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.AboutProgramButtonClicked -> {

            }

            Event.SettingsButtonClicked -> {
                onOutput(o = Output.SettingsButtonClicked)
            }

            Event.TestsButtonClicked -> {
                onOutput(o = Output.TestsButtonClicked)
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}