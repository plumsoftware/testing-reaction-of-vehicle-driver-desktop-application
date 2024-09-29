package presentation.settings.viewmodel

import data.constant.SettingsConstants
import data.model.regular.settings.Settings
import domain.storage.SettingsStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.settings.store.Effect
import presentation.settings.store.Event
import presentation.settings.store.State
import utils.showFilePicker
import java.io.File

class SettingsViewModel(
    private val settingsStorage: SettingsStorage,
) : ViewModel() {
    val state = MutableStateFlow(State())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorCoroutineContext = viewModelScope.coroutineContext + SupervisorJob()
    private val listRoots = File.listRoots().asList()

    init {
        println("Settings ViewModel created")
        val settings: Settings =
            settingsStorage.get(scope = CoroutineScope(supervisorCoroutineContext))

        state.update {
            it.copy(
                isDarkTheme = settings.isDarkTheme,
                isXlsFormat = settings.dataFormats[SettingsConstants.XLS]!!,
                isXlsxFormat = settings.dataFormats[SettingsConstants.XLSX]!!,
                isXltxFormat = settings.dataFormats[SettingsConstants.XLTX]!!,
                settings = settings,

                listRoots = listRoots,

                selectedLocalFolderToTable = File(settings.localFolderToTable)
            )
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClicked)
                }
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
                        SettingsConstants.XLSX to state.value.isXlsxFormat,
                        SettingsConstants.XLS to state.value.isXlsFormat,
                        SettingsConstants.XLTX to state.value.isXltxFormat,
                    ),
                    localFolderToTable = state.value.selectedLocalFolderToTable.path
                )
            )
        }
    }
}