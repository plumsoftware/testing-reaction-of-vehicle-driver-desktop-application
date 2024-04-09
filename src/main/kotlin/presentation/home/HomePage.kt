package presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.home.store.Event
import presentation.padding.ExtensionPadding.mediumAsymmetricalContentPadding
import presentation.padding.ExtensionPadding.mediumVerticalArrangement
import presentation.size.ConstantSize

@Composable
fun HomePage(onEvent: (Event) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = mediumVerticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                onEvent(Event.TestsButtonClicked)
            },
            modifier = Modifier
                .defaultMinSize(minWidth = ConstantSize.homeButtonWidth)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium, contentPadding = mediumAsymmetricalContentPadding
        ) {
            Text(text = "Тесты", style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            onClick = {},
            modifier = Modifier
                .defaultMinSize(minWidth = ConstantSize.homeButtonWidth)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium, contentPadding = mediumAsymmetricalContentPadding
        ) {
            Text(text = "Настройки", style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            onClick = {},
            modifier = Modifier
                .defaultMinSize(minWidth = ConstantSize.homeButtonWidth)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium,
            contentPadding = mediumAsymmetricalContentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Text(text = "О программе", style = MaterialTheme.typography.headlineMedium)
        }
    }
}