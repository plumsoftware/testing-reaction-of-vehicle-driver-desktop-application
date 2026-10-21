package newuser.login.viewmodel

import newuser.login.store.Effect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import newuser.login.store.Event
import newuser.login.store.State
import data.model.dto.test.PreTestState
import data.model.dto.test.TestDTO
import data.model.either.local.LocalEither
import data.model.regular.user.DrivingLicenseCategory
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
                    state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty ||
                    state.value.experience < 0 ||
                    state.value.age < 0 ||
                    !state.value.preTestState.isComplete
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
                                    preTestState = state.value.preTestState,
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

            is Event.OnVisionCorrectionChanged -> updatePreTestState {
                it.copy(visionCorrection = event.visionCorrection)
            }

            is Event.OnSleepHoursChanged -> updatePreTestState {
                it.copy(sleepHours = event.sleepHours?.takeIf { hours -> hours in PreTestState.sleepHoursRange })
            }

            is Event.OnWellBeingChanged -> updatePreTestState { it.copy(wellBeing = event.wellBeing) }

            is Event.OnResponseHandChanged -> updatePreTestState { it.copy(responseHand = event.responseHand) }


            is Event.OnAgeChanged -> {
                state.update {
                    it.copy(
                        age = event.age
                    )
                }
            }
        }
    }

    private fun updatePreTestState(block: (PreTestState) -> PreTestState) {
        state.update { it.copy(preTestState = block(it.preTestState)) }
    }

    private fun isErrorsStatus() {
        state.update {
            it.copy(
                isLoginError = state.value.login.isEmpty(),
                isPasswordError = state.value.password.isEmpty(),
                isDrivingLicenseCategoryError = state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty,
                isExperienceError = state.value.experience < 0,
                isAgeError = state.value.age < 0,
                showPreTestErrors = true
            )
        }
    }
}