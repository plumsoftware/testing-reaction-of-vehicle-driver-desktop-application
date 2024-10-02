package generate_code

import data.generateSecretKey
import generate_code.store.Event
import generate_code.store.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

class GenerateCodeViewModel : ViewModel() {
    val state = MutableStateFlow(State())

    init {
        println("Generate code view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.GenerateCode -> {
                try {
                    val intKeySize = state.value.keySize!!.toInt()
                if (state.value.keySize != null)
                    state.update {
                        it.copy(
                            code = generateSecretKey(keySize = intKeySize),
                            enabled = false,
                            isError = false
                        )
                    }
                else
                    state.update {
                        it.copy(isError = true)
                    }
                } catch (_: Exception) {
                    state.update {
                        it.copy(isError = true)
                    }
                }
            }

            is Event.ChangeKeySize -> {
                    state.update {
                        it.copy(keySize = event.keySize)
                    }
            }
        }
    }
}