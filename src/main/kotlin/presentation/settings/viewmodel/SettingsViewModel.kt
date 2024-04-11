package presentation.settings.viewmodel

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

    val state = MutableStateFlow(State())

    init {
        println("Settings ViewModel created")
        viewModelScope.launch(coroutineContextIO) {
            val settings: Settings = settingsStorage.get()

            state.update {
                it.copy(
                    isDarkTheme = settings.isDarkTheme,
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

            is Event.OnCheckboxChanged -> {
                state.update {
                    it.copy(
                        isDarkTheme = event.isChecked
                    )
                }

                viewModelScope.launch {
                    settingsStorage.save(settings = Settings(isDarkTheme = state.value.isDarkTheme))
                }
            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}