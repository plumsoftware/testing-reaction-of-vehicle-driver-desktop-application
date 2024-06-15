package presentation.authorization.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.authorization.auth.store.Event
import presentation.other.components.AuthTextField
import presentation.other.components.DefaultButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorizationPage(onEvent: (Event) -> Unit) {
    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackCLicked) }
            )
        },
        floatingActionButton = {
            DefaultButton(
                onClick = { onEvent(Event.StartTest) },
                content = { Text(text = "Пройти тест", style = MaterialTheme.typography.headlineMedium) })
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
            AuthTextField(labelHint = "Фамилию", onValueChange = {
                onEvent(Event.OnNameChanged(name = it))
            })
            AuthTextField(labelHint = "Имя", onValueChange = {
                onEvent(Event.OnSurnameChanged(surname = it))
            })
            AuthTextField(labelHint = "Отчество (при наличии)", onValueChange = {
                onEvent(Event.OnPatronymicChanged(patronymic = it))
            })
        }
    }
}