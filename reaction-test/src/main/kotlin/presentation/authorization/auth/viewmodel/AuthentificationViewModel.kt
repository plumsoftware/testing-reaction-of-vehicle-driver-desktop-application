package presentation.authorization.auth.viewmodel

import domain.model.regular.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.authorization.auth.store.Event
import presentation.authorization.auth.store.Output
import presentation.authorization.auth.store.State

class AuthorizationViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State(user = User.empty()))

    init {
        println("Authorization view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                onOutput(Output.BackButtonClicked)
            }

            is Event.OnNameChanged -> {
                state.update {
                    it.copy(
                        user = it.user.copy(name = event.name)
                    )
                }
            }

            is Event.OnPatronymicChanged -> {
                state.update {
                    it.copy(
                        user = it.user.copy(patronymic = event.patronymic)
                    )
                }
            }

            is Event.OnSurnameChanged -> {
                state.update {
                    it.copy(
                        user = it.user.copy(surname = event.surname)
                    )
                }
            }

            Event.StartTest -> {
                println(state.value.user.toString())
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}