package presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import data.model.regular.settings.Settings
import domain.storage.SettingsStorage
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import other.components.BackButton
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import presentation.settings.store.Effect
import presentation.settings.store.Event
import presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navigator: Navigator, settingsStorage: SettingsStorage, block: (Settings) -> Unit = {}) {

    val settingsViewModel: SettingsViewModel =
        viewModel(modelClass = SettingsViewModel::class) {
            SettingsViewModel(settingsStorage = settingsStorage,)
        }

    val state = settingsViewModel.state.collectAsState().value

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.effect.collect { effect ->
            when (effect) {
                Effect.BackClicked -> {
                    block.invoke(state.settings)
                    navigator.goBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BackButton(
                onClick = { settingsViewModel.onEvent(Event.BackClicked) }
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
                                settingsViewModel.onEvent(Event.OnCheckboxThemeChanged(isChecked = it))
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
                                settingsViewModel.onEvent(Event.OnCheckboxXlsxFormatChanged(isChecked = it))
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
                                settingsViewModel.onEvent(Event.OnCheckboxXlsFormatChanged(isChecked = it))
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
                                settingsViewModel.onEvent(Event.OnCheckboxXltxFormatChanged(isChecked = it))
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExtensionPadding.smallAsymmetricalContentPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
                        ) {
                            DropdownMenu(
                                expanded = state.dropdownMenuNetworkDriveExpanded,
                                onDismissRequest = { settingsViewModel.onEvent(Event.CollapseDropDownMenuNetworkDrive) },
                                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                            ) {
                                state.listRoots.forEachIndexed { _, file ->
                                    DropdownMenuItem(
                                        onClick = {
                                            settingsViewModel.onEvent(Event.SelectDropDownMenuNetworkDriveItem(item = file))
                                            settingsViewModel.onEvent(Event.CollapseDropDownMenuNetworkDrive)
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
                            Button(onClick = { settingsViewModel.onEvent(Event.ExpandDropDownMenuNetworkDrive) }) {
                                Text(
                                    text = state.selectedNetworkDrive.absolutePath,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(text = "Выберите сетевой диск", style = MaterialTheme.typography.bodyMedium)
                        }

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
                                        settingsViewModel.onEvent(Event.SelectLocalFolderToTable)
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
