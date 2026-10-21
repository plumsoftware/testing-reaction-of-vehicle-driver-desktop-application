package presentation.settings.viewmodel

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
import data.CryptographyKeyStore
import data.KeyScope
import presentation.settings.store.Effect
import utils.showSaveFilePicker
import presentation.settings.store.Event
import presentation.settings.store.State
import java.io.File

class SettingsViewModel(
    private val settingsStorage: SettingsStorage,
) : ViewModel() {

    val state: MutableStateFlow<State> =
        MutableStateFlow(State())
    val effect: MutableSharedFlow<Effect> = MutableSharedFlow()

    private val supervisorCoroutineContext = viewModelScope.coroutineContext + SupervisorJob()

    init {
        println("Settings ViewModel created")
        val listRoots = File.listRoots().asList()
        viewModelScope.launch(supervisorCoroutineContext) {
            val settings: Settings =
                settingsStorage.get(CoroutineScope(context = supervisorCoroutineContext))

            state.update {
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
            Event.ExportKeyClicked -> exportKey()

            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClick)
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

    /** Сохраняет ключ шифрования в config.json — для других компьютеров или резервной копии. */
    private fun exportKey() {
        val target = showSaveFilePicker(defaultName = "config.json") ?: return
        val message = try {
            CryptographyKeyStore.export(scope = KeyScope.NETWORK, target = target)
            "Ключ сохранён: ${target.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            "Не удалось сохранить ключ: ${e.message}"
        }
        state.update { it.copy(keyExportMessage = message) }
    }
}
