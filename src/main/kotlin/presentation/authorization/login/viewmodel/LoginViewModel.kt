package presentation.authorization.login.viewmodel

import domain.model.DrivingLicenseCategory
import domain.model.Gender
import domain.model.User
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
                if (
                    state.value.login.isEmpty() ||
                    state.value.password.isEmpty() ||
                    state.value.count == 0 ||
                    state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty ||
                    state.value.experience < 0
                ) {
                    state.update {
                        it.copy(
                            isLoginError = state.value.login.isEmpty(),
                            isPasswordError = state.value.password.isEmpty(),
                            isCountError = state.value.count == 0,
                            isDrivingLicenseCategoryError = state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty,
                            isExperienceError = state.value.experience < 0
                        )
                    }
                } else {
                    //Go to the database with users and get a user data
                    //MOCK DATA
                    val user = User(
                        name = "Slava",
                        surname = "Deych",
                        patronymic = "Sergeevich",
                        age = 20,
                        gender = Gender.MALE,
                        drivingLicenseCategory = DrivingLicenseCategory.NoDrivingLicense
                    )
                    onOutput(Output.OpenTestMenu)
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
        }
    }

    private fun onOutput(out: Output) {
        output(out)
    }
}