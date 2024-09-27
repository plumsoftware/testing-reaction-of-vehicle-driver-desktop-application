package authorization.login.viewmodel

import authorization.login.store.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import authorization.login.store.Event
import authorization.login.store.State
import data.model.dto.test.TestDTO
import data.model.either.local.LocalEither
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval
import data.model.regular.user.User
import domain.storage.UserStorage
import kotlinx.coroutines.flow.MutableSharedFlow

class LoginViewModel(
    private val userStorage: UserStorage
) : ViewModel() {

    val state = MutableStateFlow(State())
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Login view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackButtonClicked)
                }
            }

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
                if (
                    state.value.login.isEmpty() ||
                    state.value.password.isEmpty() ||
                    state.value.count == 0 ||
                    state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty ||
                    state.value.experience < 0 ||
                    state.value.selectedInterval == Interval()
                ) {
                    isErrorsStatus()
                } else {

                    val login: String = state.value.login
                    val password: String = state.value.password

                    viewModelScope.launch {
                        isErrorsStatus()
                        val localEither: LocalEither<Exception, User> = userStorage
                            .getUserByLoginAndPassword(login = login, password = password)
                        when (localEither) {
                            is LocalEither.Data -> {
                                val user: User = localEither
                                    .b
                                    .copy(
                                        age = state.value.age,
                                        drivingLicenseCategory = state.value.drivingLicenseCategory,
                                        experience = state.value.experience
                                    )
                                println("User to test is $user")
                                val testDto = TestDTO(
                                    user = user,
                                    count = state.value.count,
                                    interval = state.value.selectedInterval,
                                )
                                state.update {
                                    it.copy(
                                        roamingErrorMessage = ""
                                    )
                                }
                                effect.emit(Effect.OpenTestMenu(testDto = testDto))
                            }

                            is LocalEither.Exception -> {
                                state.update {
                                    it.copy(
                                        roamingErrorMessage = localEither.a.message ?: "Ошибка"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is Event.OnCountChanged -> {
                state.update {
                    it.copy(
                        count = event.count
                    )
                }
            }

            is Event.OnDrivingLicenseCategoryChanged -> {
                state.update {
                    it.copy(
                        drivingLicenseCategory = event.drivingLicenseCategory
                    )
                }
            }

            is Event.OnExperienceChanged -> {
                state.update {
                    it.copy(
                        experience = event.experience
                    )
                }
            }

            is Event.OnIntervalChanged -> {
                state.update {
                    it.copy(
                        selectedInterval = event.interval
                    )
                }
            }

            is Event.OnAgeChanged -> {
                state.update {
                    it.copy(
                        age = event.age
                    )
                }
            }
        }
    }

    private fun isErrorsStatus() {
        state.update {
            it.copy(
                isLoginError = state.value.login.isEmpty(),
                isPasswordError = state.value.password.isEmpty(),
                isCountError = state.value.count == 0,
                isDrivingLicenseCategoryError = state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty,
                isExperienceError = state.value.experience < 0,
                isIntervalError = state.value.selectedInterval == Interval(),
                isAgeError = state.value.age < 0
            )
        }
    }
}