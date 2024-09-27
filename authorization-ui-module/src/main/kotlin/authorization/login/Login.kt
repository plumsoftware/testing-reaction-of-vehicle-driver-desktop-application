package authorization.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import authorization.login.store.Effect
import authorization.login.store.Event
import authorization.login.viewmodel.LoginViewModel
import data.constant.TestConstants
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval
import domain.storage.UserStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.AuthSpinnerField
import other.components.AuthTextField
import other.components.BackButton
import other.components.DefaultButton
import other.extension.padding.ExtensionPadding
import other.extension.padding.ExtensionPadding.mediumHorizontalArrangementCenter
import other.extension.route.DesktopRouting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navigator: Navigator, userStorage: UserStorage) {

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
                    navigator.navigate(route = DesktopRouting.testmenu)
                }

                Effect.OpenPrivacyPolicy -> {
                    navigator.navigate(route = DesktopRouting.privacyPolicy)
                }
            }
        }
    }
    val state = loginViewModel.state.collectAsState()


    Scaffold(
        topBar = {
            BackButton(
                onClick = { loginViewModel.onEvent(Event.BackClicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { loginViewModel.onEvent(Event.StartTest) },
                content = {
                    Text(
                        text = "Перейти в меню с тестами",
                        style = MaterialTheme.typography.headlineMedium
                    )
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTextField(
                text = state.value.login,
                labelHint = "Логин",
                onValueChange = {
                    loginViewModel.onEvent(Event.OnLoginChanged(login = it))
                },
                isError = state.value.isLoginError
            )

            AuthTextField(
                text = state.value.password,
                labelHint = "Пароль",
                onValueChange = {
                    loginViewModel.onEvent(Event.OnPasswordChanged(password = it))
                },
                isError = state.value.isPasswordError
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthTextField(
                    text = if (state.value.experience == 0) "" else state.value.experience.toString(),
                    labelHint = "Стаж вождения",
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
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthTextField(
                    text = if (state.value.age < 0) "" else state.value.age.toString(),
                    labelHint = "Возраст",
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
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    text = if (state.value.drivingLicenseCategory == DrivingLicenseCategory.Empty) "" else state.value.drivingLicenseCategory.toString(),
                    labelHint = "Категория прав",
                    onValueChange = {
                        it as DrivingLicenseCategory
                        loginViewModel.onEvent(Event.OnDrivingLicenseCategoryChanged(it))
                    },
                    isError = state.value.isDrivingLicenseCategoryError,
                    list = DrivingLicenseCategory.entries,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    text = if (state.value.count == 0) "" else state.value.count.toString(),
                    labelHint = "Количество попыток",
                    onValueChange = {
                        it as Int
                        loginViewModel.onEvent(Event.OnCountChanged(it))
                    },
                    isError = state.value.isCountError,
                    list = TestConstants.counts.toList(),
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
            }

            AuthSpinnerField(
                text = if (state.value.selectedInterval == Interval()) "" else state.value.selectedInterval.toString(),
                labelHint = "Интервал сигнала в секундах",
                onValueChange = {
                    loginViewModel.onEvent(Event.OnIntervalChanged(it as Interval))
                },
                isError = state.value.isIntervalError,
                list = TestConstants.Tests.intervals.toList(),
                modifier = Modifier.wrapContentWidth().wrapContentHeight().align(Alignment.Start)
            )

            //Карточка с ошибкой
            AnimatedVisibility(
                visible = state.value.roamingErrorMessage.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                ) {
                    Text(
                        text = state.value.roamingErrorMessage,
                        modifier = Modifier.padding(ExtensionPadding.smallPadding),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}