package presentation.authorization.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.model.DrivingLicenseCategory
import presentation.authorization.login.store.Event
import presentation.authorization.login.store.State
import presentation.other.components.AuthSpinnerField
import presentation.other.components.AuthTextField
import presentation.other.components.BackButton
import presentation.other.components.DefaultButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.padding.ExtensionPadding.mediumHorizontalArrangementCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onEvent: (Event) -> Unit,
    state: androidx.compose.runtime.State<State>,
) {
    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackClicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { onEvent(Event.StartTest) },
                content = { Text(text = "Перейти в меню с тестами", style = MaterialTheme.typography.headlineMedium) })
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
                labelHint = "Логин",
                onValueChange = {
                    onEvent(Event.OnLoginChanged(login = it))
                },
                isError = state.value.isLoginError
            )

            AuthTextField(
                labelHint = "Пароль",
                onValueChange = {
                    onEvent(Event.OnPasswordChanged(password = it))
                },
                isError = state.value.isPasswordError
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = mediumHorizontalArrangementCenter,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AuthTextField(
                    labelHint = "Стаж воздения",
                    onValueChange = {
                        onEvent(Event.OnExperienceChanged(experience = try { it.toInt() } catch (e: Exception) { 0 }))
                    },
                    isError = state.value.isExperienceError,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    labelHint = "Категория прав",
                    onValueChange = {
                        it as DrivingLicenseCategory
                        onEvent(Event.OnDrivingLicenseCategoryChanged(it))
                    },
                    isError = state.value.isDrivingLicenseCategoryError,
                    list = DrivingLicenseCategory.entries,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                AuthSpinnerField(
                    labelHint = "Количество попыток",
                    onValueChange = {
                        it as Int
                        onEvent(Event.OnCountChanged(it))
                    },
                    isError = state.value.isCountError,
                    list = listOf(10, 50, 100, 150, 200, 250, 300),
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
            }

        }
    }
}