package presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.other.components.BackButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.other.extension.size.ConstantSize
import presentation.settings.store.Event
import presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(onEvent: (Event) -> Unit, settingsViewModel: SettingsViewModel) {

    val state = settingsViewModel.state.collectAsState().value

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
                    Text(text = "Специальные настройки", style = MaterialTheme.typography.headlineSmall)
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        border = BorderStroke(
                            ConstantSize.regularBorderWidth,
                            MaterialTheme.colorScheme.errorContainer
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            DropdownMenu(
                                expanded = state.dropdownMenuNetworkDriveExpanded,
                                onDismissRequest = { onEvent(Event.CollapseDropDownMenuNetworkDrive) },
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                            ) {
                                state.listRoots.forEachIndexed { _, file ->
                                    DropdownMenuItem(
                                        onClick = {
                                            onEvent(Event.SelectDropDownMenuNetworkDriveItem(item = file))
                                            onEvent(Event.CollapseDropDownMenuNetworkDrive)
                                        },
                                    ) {
                                        Text(
                                            text = file.absolutePath,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                            Button(onClick = { onEvent(Event.ExpandDropDownMenuNetworkDrive) }) {
                                Text(
                                    text = state.selectedNetworkDrive.absolutePath,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(text = "Выберите сетевой диск", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}