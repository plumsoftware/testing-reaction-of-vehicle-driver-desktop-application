package presentation.settings.viewmodel

import androidx.compose.runtime.MutableState
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
import kotlin.coroutines.CoroutineContext

class SettingsViewModel(
    private val output: (Output) -> Unit,
    private val settingsStorage: SettingsStorage,
    coroutineContextIO: CoroutineContext,
) : ViewModel() {

    val state: MutableStateFlow<State> =  MutableStateFlow(State())

    init {
        println("Settings ViewModel created")
        viewModelScope.launch(coroutineContextIO) {
            val settings: Settings = settingsStorage.get()

            state.update {
                it.copy(
                    isDarkTheme = settings.isDarkTheme,
                    isXlsFormat = settings.dataFormats[Constants.XLS]!!,
                    isXlsxFormat = settings.dataFormats[Constants.XLSX]!!,
                    settings = settings
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
        }
    }

    private fun save(state: MutableStateFlow<State>) {
        viewModelScope.launch {
            settingsStorage.save(
                settings = Settings(
                    isDarkTheme = state.value.isDarkTheme,
                    dataFormats = mapOf(
                        Constants.XLSX to state.value.isXlsxFormat,
                        Constants.XLS to state.value.isXlsFormat
                    )
                )
            )
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}