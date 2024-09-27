package authorization.auth.viewmodel

import authorization.auth.store.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import authorization.auth.store.Event
import authorization.auth.store.State
import data.model.regular.user.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class AuthorizationViewModel : ViewModel() {

    val state = MutableStateFlow(State(user = User.empty()))
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Authorization view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.GoBack)
                }
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
}