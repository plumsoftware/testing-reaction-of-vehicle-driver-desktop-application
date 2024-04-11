package presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.components.BackButton
import presentation.components.DefaultButton
import presentation.home.store.Event
import presentation.extension.padding.ExtensionPadding.mediumVerticalArrangement

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
                    Text(text = "Тесты", style = MaterialTheme.typography.headlineMedium)
                },
                onClick = {
                    onEvent(Event.TestsButtonClicked)
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
                    onEvent(Event.TestsButtonClicked)
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