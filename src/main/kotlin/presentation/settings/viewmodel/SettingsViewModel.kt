package presentation.settings.viewmodel

import data.Constants
import domain.model.regular.settings.Settings
import domain.storage.SettingsStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.settings.store.Event
import presentation.settings.store.Output
import presentation.settings.store.State
import utils.showFilePicker
import java.io.File
import kotlin.coroutines.CoroutineContext

class SettingsViewModel(
    private val output: (Output) -> Unit,
    private val settingsStorage: SettingsStorage,
    coroutineContextIO: CoroutineContext,
) : ViewModel() {
    private val listRoots = File.listRoots().asList()

    val state: MutableStateFlow<State> = MutableStateFlow(State(selectedNetworkDrive = listRoots[0]))

    init {
        println("Settings ViewModel created")
        viewModelScope.launch(coroutineContextIO) {
            val settings: Settings = settingsStorage.get(CoroutineScope(coroutineContextIO))

            state.update {

                /**
                 *  Initial state for the settings
                 * */

                it.copy(
                    isDarkTheme = settings.isDarkTheme,
                    isXlsFormat = settings.dataFormats[Constants.Settings.XLS]!!,
                    isXlsxFormat = settings.dataFormats[Constants.Settings.XLSX]!!,
                    isXltxFormat = settings.dataFormats[Constants.Settings.XLTX]!!,
                    settings = settings,

                    listRoots = listRoots,
                    selectedNetworkDrive = if (settings.networkDrive.isNotEmpty()) File(settings.networkDrive) else listRoots[0],
                    selectedLocalDrive = if (settings.localDrive.isNotEmpty()) File(settings.localDrive) else listRoots[0],

                    selectedLocalFolderToTable = File(settings.localFolderToTable)
                )
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                onOutput(Output.BackClicked)
            }

//            region::Check boxes
            is Event.OnCheckboxThemeChanged -> {
                state.update {
                    it.copy(
                        isDarkTheme = event.isChecked
                    )
                }
                save(state = state)
            }

            is Event.OnCheckboxXlsFormatChanged -> {
                state.update {
                    it.copy(
                        isXlsFormat = event.isChecked
                    )
                }
                save(state = state)
            }

            is Event.OnCheckboxXlsxFormatChanged -> {
                state.update {
                    it.copy(
                        isXlsxFormat = event.isChecked
                    )
                }
                save(state = state)
            }

            is Event.OnCheckboxXltxFormatChanged -> {
                state.update {
                    it.copy(
                        isXltxFormat = event.isChecked
                    )
                }
                save(state = state)
            }
//            endregion

//            region::Network drive
            Event.CollapseDropDownMenuNetworkDrive -> {
                state.update {
                    it.copy(
                        dropdownMenuNetworkDriveExpanded = false
                    )
                }
            }

            Event.ExpandDropDownMenuNetworkDrive -> {
                state.update {
                    it.copy(
                        dropdownMenuNetworkDriveExpanded = true
                    )
                }
            }

            is Event.SelectDropDownMenuNetworkDriveItem -> {
                state.update {
                    it.copy(
                        selectedNetworkDrive = event.item
                    )
                }
                save(state = state)
            }
//            endregion

//            region::Local drive
            Event.CollapseDropDownMenuLocalDrive -> {
                state.update {
                    it.copy(
                        dropdownMenuLocalDriveExpanded = false
                    )
                }
            }

            Event.ExpandDropDownMenuLocalDrive -> {
                state.update {
                    it.copy(
                        dropdownMenuLocalDriveExpanded = true
                    )
                }
            }

            is Event.SelectDropDownMenuLocalDriveItem -> {
                state.update {
                    it.copy(
                        selectedLocalDrive = event.item
                    )
                }
                save(state = state)
            }
//            endregion

//            region::Local folder
            Event.SelectLocalFolderToTable -> {
                val selectedLocalFolderToTable: File = showFilePicker()

                state.update {
                    it.copy(
                        selectedLocalFolderToTable = selectedLocalFolderToTable
                    )
                }
                save(state = state)
            }
//            endregion
        }
    }

    private fun save(state: MutableStateFlow<State>) {
        viewModelScope.launch {
            settingsStorage.save(
                settings = Settings(
                    isDarkTheme = state.value.isDarkTheme,
                    dataFormats = mapOf(
                        Constants.Settings.XLSX to state.value.isXlsxFormat,
                        Constants.Settings.XLS to state.value.isXlsFormat,
                        Constants.Settings.XLTX to state.value.isXltxFormat,
                    ),
                    networkDrive = "${state.value.selectedNetworkDrive.absolutePath}\\",
                    localDrive = "${state.value.selectedLocalDrive.absolutePath}\\",
                    localFolderToTable = state.value.selectedLocalFolderToTable.path
                )
            )
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}