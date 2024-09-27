package presentation.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.main.store.MainState

class MainViewModel : ViewModel() {

    val state = MutableStateFlow(MainState())

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
        }
    }
}