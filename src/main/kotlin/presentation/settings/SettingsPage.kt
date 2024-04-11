package presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.components.BackButton
import presentation.extension.padding.ExtensionPadding
import presentation.settings.store.Event
import presentation.settings.store.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(onEvent: (Event) -> Unit, _state: MutableStateFlow<State>) {

    val state = _state.collectAsState().value

    Scaffold(
        topBar = {
            BackButton(
                onClick = { onEvent(Event.BackClicked) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = ExtensionPadding.scaffoldTopPadding)
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Внешний вид", style = MaterialTheme.typography.headlineSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                    ) {
                        Text(text = "Тёмная тема", style = MaterialTheme.typography.bodyMedium)
                        Checkbox(checked = state.isDarkTheme, onCheckedChange = {
                            onEvent(Event.OnCheckboxChanged(isChecked = it))
                        })
                    }
                }
            }
        }
    }
}