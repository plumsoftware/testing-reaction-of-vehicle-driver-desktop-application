package presentation.privacy.viewmodel

import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.privacy.store.Event
import presentation.privacy.store.Output

class PrivacyPolicyViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {
    init {
        println("Privacy policy view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                onOutput(Output.BackButtonClicked)
            }
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}