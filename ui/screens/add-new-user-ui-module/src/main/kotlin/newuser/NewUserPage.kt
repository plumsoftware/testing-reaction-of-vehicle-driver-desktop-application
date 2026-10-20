package newuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import other.components.FormMessage
import other.components.FormScreen
import other.components.FormSection
import theme.ExtendedTheme

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

    FormScreen(
        title = "Новый пользователь",
        subtitle = "Заполните данные водителя. Логин и пароль понадобятся ему для входа в тестирование",
        primaryButtonText = "Сохранить",
        onPrimaryClick = { newUserViewModel.onEvent(Event.SaveNewUser) },
        onBackClick = { newUserViewModel.onEvent(Event.BackCLicked) },
        message = {
            val result = state.value.appEither
            AnimatedVisibility(
                visible = result !is AppEither.Handle,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                when (result) {
                    is AppEither.Exception<*> -> FormMessage(text = result.e.toString(), isError = true)
                    is AppEither.Success<*> -> FormMessage(
                        text = result.e.toString(),
                        isError = false,
                        successContainerColor = ExtendedTheme.colors.successContainer,
                        onSuccessContainerColor = ExtendedTheme.colors.onSuccessContainer
                    )
                    AppEither.Handle -> {}
                }
            }
        }
    ) {
        FormSection(title = "Личные данные") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthTextField(
                    labelHint = "Фамилию",
                    label = "Фамилия",
                    leadingIcon = Icons.Rounded.Person,
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnSurnameChanged(surname = it))
                    },
                    modifier = Modifier.weight(1.0f),
                    isError = state.value.isSurnameError
                )
                AuthTextField(
                    labelHint = "Имя",
                    label = "Имя",
                    leadingIcon = Icons.Rounded.Person,
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnNameChanged(name = it))
                    },
                    modifier = Modifier.weight(1.0f),
                    isError = state.value.isNameError
                )
                AuthTextField(
                    labelHint = "Отчество (при наличии)",
                    label = "Отчество (при наличии)",
                    leadingIcon = Icons.Rounded.Person,
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnPatronymicChanged(patronymic = it))
                    },
                    modifier = Modifier.weight(1.0f),
                )
            }
            AuthSpinnerField(
                text = if (state.value.gender == Gender.EMPTY) "" else state.value.gender.toString(),
                labelHint = "Пол",
                label = "Пол",
                leadingIcon = Icons.Rounded.Face,
                onValueChange = {
                    it as Gender
                    newUserViewModel.onEvent(Event.OnGenderChanged(it))
                },
                isError = state.value.isGenderError,
                list = Gender.entries,
                modifier = Modifier.fillMaxWidth(0.333f),
            )
        }

        FormSection(title = "Учётная запись") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AuthTextField(
                    labelHint = "Логин",
                    label = "Логин",
                    leadingIcon = Icons.Rounded.AccountCircle,
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnLoginChanged(login = it))
                    },
                    modifier = Modifier.weight(1.0f),
                    isError = state.value.isLoginError
                )
                AuthTextField(
                    labelHint = "Пароль",
                    label = "Пароль",
                    leadingIcon = Icons.Rounded.Lock,
                    isPassword = true,
                    onValueChange = {
                        newUserViewModel.onEvent(Event.OnPasswordChanged(password = it))
                    },
                    modifier = Modifier.weight(1.0f),
                    isError = state.value.isPasswordError
                )
            }
        }
    }
}
