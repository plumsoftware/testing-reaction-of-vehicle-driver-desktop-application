package presentation.settings.viewmodel

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
                    settings = settings,

                    listRoots = listRoots,
                    selectedNetworkDrive = if (settings.networkDrive.isNotEmpty()) File(settings.networkDrive) else listRoots[0],
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
        }
    }

    private fun save(state: MutableStateFlow<State>) {
        viewModelScope.launch {
            settingsStorage.save(
                settings = Settings(
                    isDarkTheme = state.value.isDarkTheme,
                    networkDrive = "${state.value.selectedNetworkDrive.absolutePath}\\",
                )
            )
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}