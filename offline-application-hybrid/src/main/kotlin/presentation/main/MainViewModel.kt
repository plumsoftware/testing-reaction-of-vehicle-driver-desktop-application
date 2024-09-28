package presentation.main

import domain.storage.SettingsStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.main.store.Event
import presentation.main.store.MainState

class MainViewModel(
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    val state = MutableStateFlow(MainState())
    private val supervisorCoroutineContext = viewModelScope.coroutineContext + SupervisorJob()

    init {
        println("Main view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.ChangeTestDto -> {
                state.update {
                    it.copy(
                        testDTO = event.testDto
                    )
                }
            }

            Event.LoadSettings -> {
                val settings =
                    settingsStorage.get(scope = CoroutineScope(supervisorCoroutineContext))

                state.update {
                    it.copy(settings = settings)
                }
            }

            is Event.ChangeSelectedUser -> {
                state.update {
                    it.copy(selectedUser = event.user)
                }
            }
        }
    }
}