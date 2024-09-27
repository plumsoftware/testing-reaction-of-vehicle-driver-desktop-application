package presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
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
            verticalArrangement = ExtensionPadding.largeVerticalArrangementTop,
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            Checkbox(checked = state.isDarkTheme, onCheckedChange = {
                                onEvent(Event.OnCheckboxThemeChanged(isChecked = it))
                            })
                            Text(text = "Тёмная тема", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Форматы экспорта", style = MaterialTheme.typography.headlineSmall)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            Checkbox(checked = state.isXlsxFormat, onCheckedChange = {
                                onEvent(Event.OnCheckboxXlsxFormatChanged(isChecked = it))
                            })
                            Text(text = ".XLSX", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            Checkbox(checked = state.isXlsFormat, onCheckedChange = {
                                onEvent(Event.OnCheckboxXlsFormatChanged(isChecked = it))
                            })
                            Text(text = ".XLS", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            Checkbox(checked = state.isXltxFormat, onCheckedChange = {
                                onEvent(Event.OnCheckboxXltxFormatChanged(isChecked = it))
                            })
                            Text(text = ".XLTX", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .wrapContentSize(),
                    verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Специальные настройки", style = MaterialTheme.typography.headlineSmall)
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        border = BorderStroke(
                            ConstantSize.regularBorderWidth,
                            MaterialTheme.colorScheme.errorContainer
                        ),
                    ) {

                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(ExtensionPadding.smallVerticalContentPadding),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Выберите место для локального сохранения таблицы",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(ExtensionPadding.mediumSymmetricalContentPadding)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onEvent(Event.SelectLocalFolderToTable)
                                    }
                                ) {
                                    Text(
                                        text = state.selectedLocalFolderToTable.path.toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}