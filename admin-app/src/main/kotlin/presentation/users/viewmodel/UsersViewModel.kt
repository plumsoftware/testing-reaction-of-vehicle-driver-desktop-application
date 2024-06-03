package presentation.users.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.users.store.Event
import presentation.users.store.Output
import presentation.users.store.State

class UsersViewModel(
    private val output: (Output) -> Unit
) : ViewModel() {

    val state = MutableStateFlow(State())

    fun onEvent(event: Event) {
        when (event) {
            else -> {}
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}