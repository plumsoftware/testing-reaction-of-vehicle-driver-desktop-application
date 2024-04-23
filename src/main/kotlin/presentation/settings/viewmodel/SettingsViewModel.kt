package presentation.settings.viewmodel

import data.Constants
import domain.model.Settings
import domain.storage.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.settings.store.Event
import presentation.settings.store.Output
import presentation.settings.store.State
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
            val settings: Settings = settingsStorage.get()

            state.update {

                it.copy(
                    isDarkTheme = settings.isDarkTheme,
                    isXlsFormat = settings.dataFormats[Constants.Settings.XLS]!!,
                    isXlsxFormat = settings.dataFormats[Constants.Settings.XLSX]!!,
                    settings = settings,

                    listRoots = listRoots,
                    selectedNetworkDrive = if(settings.networkDrive.isNotEmpty()) File(settings.networkDrive) else listRoots[0]
                )
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                onOutput(Output.BackClicked)
            }

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

            Event.CollapseDropDownMenu -> {
                state.update {
                    it.copy(
                        dropdownMenuExpanded = false
                    )
                }
            }

            Event.ExpandDropDownMenu -> {
                state.update {
                    it.copy(
                        dropdownMenuExpanded = true
                    )
                }
            }

            is Event.SelectDropDownMenuItem -> {
                state.update {
                    it.copy(
                        selectedNetworkDrive = event.item
                    )
                }
                save(state = state)
            }
        }
    }

    private fun save(state: MutableStateFlow<State>) {
        viewModelScope.launch {
            settingsStorage.save(
                settings = Settings(
                    isDarkTheme = state.value.isDarkTheme,
                    dataFormats = mapOf(
                        Constants.Settings.XLSX to state.value.isXlsxFormat,
                        Constants.Settings.XLS to state.value.isXlsFormat
                    ),
                    networkDrive = "${state.value.selectedNetworkDrive.absolutePath}\\"
                )
            )
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}