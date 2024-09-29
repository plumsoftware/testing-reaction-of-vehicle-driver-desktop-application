package newuser

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.either.AppEither
import data.model.regular.user.Gender
import domain.storage.UserStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import newuser.store.Effect
import newuser.store.Event
import newuser.viewmodel.NewUserViewModel
import other.components.AuthSpinnerField
import other.components.AuthTextField
import other.components.BackButton
import other.components.DefaultButton
import other.extension.padding.ExtensionPadding
import theme.ExtendedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUserPage(navigator: Navigator, userStorage: UserStorage) {

    val newUserViewModel: NewUserViewModel =
        viewModel(modelClass = NewUserViewModel::class) {
            NewUserViewModel(
                userStorage = userStorage
            )
        }

    val state = newUserViewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        newUserViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> {
                    navigator.goBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { newUserViewModel.onEvent(Event.BackCLicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { newUserViewModel.onEvent(Event.SaveNewUser) },
                content = {
                    Text(
                        text = "Сохранить",
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthTextField(
                    labelHint = "Фамилию",
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnSurnameChanged(surname = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isSurnameError
                )
                AuthTextField(
                    labelHint = "Имя",
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnNameChanged(name = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isNameError
                )
                AuthTextField(
                    labelHint = "Отчество (при наличии)",
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnPatronymicChanged(patronymic = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthTextField(
                    labelHint = "Логин",
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnLoginChanged(login = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isLoginError
                )
                AuthTextField(
                    labelHint = "Пароль",
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnPasswordChanged(password = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isPasswordError
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthSpinnerField(
                    text = if (state.value.gender == Gender.EMPTY) "" else state.value.gender.toString(),
                    labelHint = "Пол",
                    onValueChange = {
                        it as Gender
                        newUserViewModel.onEvent(Event.OnGenderChanged(it))
                    },
                    isError = state.value.isGenderError,
                    list = Gender.entries,
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                )
            }

            when (val result = state.value.appEither) {
                is AppEither.Exception<*> -> {
                    AnimatedVisibility(
                        visible = state.value.appEither == AppEither.Exception(result.e),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
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
                                text = result.e.toString(),
                                modifier = Modifier.padding(ExtensionPadding.smallPadding),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                AppEither.Handle -> {}
                is AppEither.Success<*> -> {
                    AnimatedVisibility(
                        visible = result == AppEither.Success(result.e),
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ExtendedTheme.colors.successContainer,
                                contentColor = ExtendedTheme.colors.onSuccessContainer
                            ),
                            modifier = Modifier
                                .wrapContentWidth()
                        ) {
                            Text(
                                text = result.e.toString(),
                                modifier = Modifier.padding(ExtensionPadding.smallPadding),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}