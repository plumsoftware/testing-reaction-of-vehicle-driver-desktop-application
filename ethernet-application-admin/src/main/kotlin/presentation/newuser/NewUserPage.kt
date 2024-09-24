package presentation.newuser

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.model.either.AppEither
import domain.model.regular.user.Gender
import presentation.newuser.store.Event
import presentation.newuser.viewmodel.NewUserViewModel
import presentation.other.components.AuthSpinnerField
import presentation.other.components.AuthTextField
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.theme.ExtendedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUserPage(onEvent: (Event) -> Unit, newUserViewModel: NewUserViewModel) {

    val state = newUserViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackCLicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { onEvent(Event.SaveNewUser) },
                content = { Text(text = "Сохранить", style = MaterialTheme.typography.headlineMedium) })
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
                        onEvent(Event.OnSurnameChanged(surname = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isSurnameError
                )
                AuthTextField(
                    labelHint = "Имя",
                    onValueChange = {
                        onEvent(Event.OnNameChanged(name = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isNameError
                )
                AuthTextField(
                    labelHint = "Отчество (при наличии)",
                    onValueChange = {
                        onEvent(Event.OnPatronymicChanged(patronymic = it))
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
                        onEvent(Event.OnLoginChanged(login = it))
                    },
                    modifier = Modifier.fillMaxWidth().weight(1.0f),
                    isError = state.value.isLoginError
                )
                AuthTextField(
                    labelHint = "Пароль",
                    onValueChange = {
                        onEvent(Event.OnPasswordChanged(password = it))
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
                        onEvent(Event.OnGenderChanged(it))
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