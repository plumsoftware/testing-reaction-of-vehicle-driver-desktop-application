package presentation.authorization.login.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.Output
import presentation.authorization.login.store.State

class LoginViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Login view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> onOutput(Output.BackButtonClicked)

            is Event.OnLoginChanged -> {
                state.update {
                    it.copy(
                        login = event.login
                    )
                }
            }

            is Event.OnPasswordChanged -> {
                state.update {
                    it.copy(
                        password = event.password
                    )
                }
            }

            Event.StartTest -> {
                onOutput(Output.OpenTestMenu)
            }
        }
    }

    private fun onOutput(out: Output) {
        output(out)
    }
}