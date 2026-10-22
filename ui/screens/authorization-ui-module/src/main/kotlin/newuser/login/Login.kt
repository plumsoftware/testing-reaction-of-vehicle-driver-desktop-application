package newuser.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import newuser.login.store.Effect
import newuser.login.store.Event
import newuser.login.viewmodel.LoginViewModel
import data.model.dto.test.PreTestState
import data.model.dto.test.ResponseHand
import data.model.dto.test.TestDTO
import data.model.dto.test.VisionCorrection
import data.model.regular.user.DrivingLicenseCategory
import domain.storage.UserStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.AuthSpinnerField
import other.components.AuthTextField
import other.components.FormMessage
import other.components.FormScreen
import other.components.FormSection
import other.extension.route.DesktopRouting

@Composable
fun Login(navigator: Navigator, userStorage: UserStorage, block: (TestDTO) -> Unit = {}) {

    val loginViewModel: LoginViewModel = viewModel(modelClass = LoginViewModel::class) {
        LoginViewModel(
            userStorage = userStorage
        )
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackButtonClicked -> {
                    navigator.goBack()
                }

                is Effect.OpenTestMenu -> {
                    block.invoke(effect.testDto)
                    navigator.navigate(route = DesktopRouting.testmenu)
                }

                Effect.OpenPrivacyPolicy -> {
                    navigator.navigate(route = DesktopRouting.privacyPolicy)
                }
            }
        }
    }
    val state = loginViewModel.state.collectAsState()


    FormScreen(
        title = "Вход в тестирование",
        subtitle = "Введите логин и пароль, выданные администратором, и параметры теста",
        primaryButtonText = "Перейти к тестам",
        onPrimaryClick = { loginViewModel.onEvent(Event.StartTest) },
        onBackClick = { loginViewModel.onEvent(Event.BackClicked) },
        message = {
            //Карточка с ошибкой
            AnimatedVisibility(
                visible = state.value.roamingErrorMessage.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                FormMessage(text = state.value.roamingErrorMessage, isError = true)
            }
        }
    ) {
        FormSection(title = "Учётная запись") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthTextField(
                    text = state.value.login,
                    labelHint = "логин",
                    label = "Логин",
                    leadingIcon = Icons.Rounded.AccountCircle,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnLoginChanged(login = it))
                    },
                    isError = state.value.isLoginError,
                    modifier = Modifier.weight(1.0f)
                )
                AuthTextField(
                    text = state.value.password,
                    labelHint = "пароль",
                    label = "Пароль",
                    leadingIcon = Icons.Rounded.Lock,
                    isPassword = true,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnPasswordChanged(password = it))
                    },
                    isError = state.value.isPasswordError,
                    modifier = Modifier.weight(1.0f)
                )
            }
        }

        FormSection(title = "О водителе") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthTextField(
                    text = if (state.value.experience == 0) "" else state.value.experience.toString(),
                    labelHint = "стаж вождения",
                    label = "Стаж вождения, лет",
                    leadingIcon = Icons.Rounded.DateRange,
                    onValueChange = {
                        loginViewModel.onEvent(
                            Event.OnExperienceChanged(
                                experience = try {
                                    it.toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            )
                        )
                    },
                    isError = state.value.isExperienceError,
                    modifier = Modifier.weight(1.0f)
                )
                AuthTextField(
                    text = if (state.value.age < 0) "" else state.value.age.toString(),
                    labelHint = "возраст",
                    label = "Возраст",
                    leadingIcon = Icons.Rounded.Person,
                    onValueChange = {
                        loginViewModel.onEvent(
                            Event.OnAgeChanged(
                                age = try {
                                    it.toInt()
                                } catch (e: Exception) {
                                    -1
                                }
                            )
                        )
                    },
                    isError = state.value.isAgeError,
                    modifier = Modifier.weight(1.0f)
                )
                AuthSpinnerField(
                    text = if (state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty) "" else state.value.drivingLicenseCategory.toString(),
                    labelHint = "категорию прав",
                    label = "Категория прав",
                    leadingIcon = Icons.Rounded.AccountBox,
                    onValueChange = {
                        it as DrivingLicenseCategory
                        loginViewModel.onEvent(Event.OnDrivingLicenseCategoryChanged(it))
                    },
                    isError = state.value.isDrivingLicenseCategoryError,
                    list = DrivingLicenseCategory.entries,
                    modifier = Modifier.weight(1.0f)
                )
            }
        }

        FormSection(title = "Состояние перед тестом") {
            val preTest = state.value.preTestState
            val showErrors = state.value.showPreTestErrors
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthSpinnerField(
                    text = preTest.visionCorrection?.toString() ?: "",
                    labelHint = "коррекцию зрения",
                    label = "Очки / линзы",
                    leadingIcon = Icons.Rounded.Face,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnVisionCorrectionChanged(it as VisionCorrection))
                    },
                    isError = showErrors && preTest.visionCorrection == null,
                    list = VisionCorrection.entries,
                    modifier = Modifier.weight(1.0f)
                )
                AuthTextField(
                    text = preTest.sleepHours?.toString() ?: "",
                    labelHint = "часы сна",
                    label = "Сон за сутки, ч",
                    leadingIcon = Icons.Rounded.Star,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnSleepHoursChanged(it.trim().toIntOrNull()))
                    },
                    isError = showErrors && preTest.sleepHours == null,
                    modifier = Modifier.weight(1.0f)
                )
                AuthSpinnerField(
                    text = preTest.wellBeing?.toString() ?: "",
                    labelHint = "самочувствие",
                    label = "Самочувствие: 1 — плохо, 5 — отлично",
                    leadingIcon = Icons.Rounded.Favorite,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnWellBeingChanged(it as Int))
                    },
                    isError = showErrors && preTest.wellBeing == null,
                    list = PreTestState.wellBeingScale.toList(),
                    modifier = Modifier.weight(1.0f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthSpinnerField(
                    text = preTest.responseHand?.toString() ?: "",
                    labelHint = "руку для ответа",
                    label = "Рука для ответа",
                    leadingIcon = Icons.Rounded.ThumbUp,
                    onValueChange = {
                        loginViewModel.onEvent(Event.OnResponseHandChanged(it as ResponseHand))
                    },
                    isError = showErrors && preTest.responseHand == null,
                    list = ResponseHand.entries,
                    modifier = Modifier.weight(1.0f)
                )
            }
        }
    }
}
