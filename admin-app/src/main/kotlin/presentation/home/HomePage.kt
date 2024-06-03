package presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.other.components.DefaultButton
import presentation.home.store.Event
import presentation.other.extension.padding.ExtensionPadding.mediumVerticalArrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(onEvent: (Event) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = mediumVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                content = {
                    Text(text = "Пользователи", style = MaterialTheme.typography.headlineMedium)
                },
                onClick = {
                    onEvent(Event.UsersButtonClicked)
                },
            )
            DefaultButton(
                content = {
                    Text(text = "Добавить нового пользователя", style = MaterialTheme.typography.headlineMedium)
                },
                onClick = {
                    onEvent(Event.AddNewUserButtonClicked)
                },
            )
            DefaultButton(
                content = {
                    Text(text = "Настройки", style = MaterialTheme.typography.headlineMedium)
                },
                onClick = {
                    onEvent(Event.SettingsButtonClicked)
                },
            )
            DefaultButton(
                onClick = {
                    onEvent(Event.AddNewUserButtonClicked)
                },
                content = {
                    Text(text = "О программе", style = MaterialTheme.typography.headlineMedium)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}