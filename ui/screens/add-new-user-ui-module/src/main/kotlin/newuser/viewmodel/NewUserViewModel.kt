package newuser.viewmodel

import data.model.either.AppEither
import data.model.regular.user.Gender
import data.model.regular.user.User
import domain.storage.UserStorage
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import newuser.store.Effect
import newuser.store.Event
import newuser.store.State

class NewUserViewModel(
    private val userStorage: UserStorage
) : ViewModel() {

    val state = MutableStateFlow(State())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorCoroutineContext = viewModelScope.coroutineContext + SupervisorJob()

    init {
        println("New user view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                clearState()
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
            }

            is Event.OnGenderChanged -> {
                state.update {
                    it.copy(
                        gender = event.gender
                    )
                }
            }

            is Event.OnLoginChanged -> state.update {
                it.copy(
                    login = event.login
                )
            }

            is Event.OnNameChanged -> {
                state.update {
                    it.copy(
                        name = event.name
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

            is Event.OnPatronymicChanged -> {
                state.update {
                    it.copy(
                        patronymic = event.patronymic
                    )
                }
            }

            is Event.OnSurnameChanged -> {
                state.update {
                    it.copy(
                        surname = event.surname
                    )
                }
            }

            Event.SaveNewUser -> {
                if (!checkErrors()) {
                    val newUser = User(
                        name = state.value.name,
                        surname = state.value.surname,
                        patronymic = state.value.patronymic,
                        gender = state.value.gender,
                        login = state.value.login,
                        password = state.value.password
                    )
                    viewModelScope.launch(context = supervisorCoroutineContext) {
                        val isPasswordUnique = userStorage.isPasswordUnique(state.value.password)
                        if (isPasswordUnique) {
                            try {
                                userStorage.insert(user = newUser)

                                state.update {
                                    it.copy(appEither = AppEither.Success("Пользователь добавлен"))
                                }
                                delay(5000)
                                state.update {
                                    it.copy(appEither = AppEither.Handle)
                                }
                            } catch (e: Exception) {
                                state.update {
                                    it.copy(appEither = AppEither.Exception(e))
                                }
                            }
                        } else {
                            state.update {
                                it.copy(appEither = AppEither.Exception("Такой пароль уже существует"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkErrors(): Boolean {
        state.update {
            it.copy(
                isNameError = state.value.name.isEmpty(),
                isSurnameError = state.value.surname.isEmpty(),
                isLoginError = state.value.login.isEmpty(),
                isPasswordError = state.value.password.isEmpty(),
                isGenderError = state.value.gender == Gender.EMPTY
            )
        }

        return state.value.name.isEmpty() ||
                state.value.surname.isEmpty() ||
                state.value.login.isEmpty() ||
                state.value.gender == Gender.EMPTY
    }

    private fun clearState() {
        state.update {
            it.copy(
                name = "",
                surname = "",
                patronymic = "",
                gender = Gender.EMPTY,
                login = "",
                password = "",

                isNameError = false,
                isSurnameError = false,
                isGenderError = false,
                isLoginError = false,
                isPasswordError = false,
            )
        }
    }
}