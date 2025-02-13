package about_app

import about_app.store.Effect
import about_app.store.Event
import about_app.store.State
import getProgramData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AboutAppViewModel : ViewModel() {

    private val programData = getProgramData()

    val state = MutableStateFlow(
        State(
            version = programData.version,
            name = programData.name,
            number = programData.number
        )
    )
    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClick -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClick)
                }
            }
        }
    }
}