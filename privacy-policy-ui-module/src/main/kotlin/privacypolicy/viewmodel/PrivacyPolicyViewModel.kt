package privacypolicy.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import privacypolicy.store.Effect
import privacypolicy.store.Event
import privacypolicy.store.State

class PrivacyPolicyViewModel : ViewModel() {

    val state = MutableStateFlow(State())
    val effect = MutableSharedFlow<Effect>()

    init {
        println("Privacy policy view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackCLicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackClick)
                }
            }
        }
    }
}