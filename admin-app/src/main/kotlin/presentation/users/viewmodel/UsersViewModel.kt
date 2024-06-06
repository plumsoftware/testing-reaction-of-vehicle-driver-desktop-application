package presentation.users.viewmodel

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.User
import domain.storage.SQLDeLightStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.users.store.Event
import presentation.users.store.Output
import presentation.users.store.State
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class UsersViewModel(
    private val output: (Output) -> Unit,
    private val sqlDeLightStorage: SQLDeLightStorage,
    private val coroutineContextIO: CoroutineContext
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Users view model created")

        viewModelScope.launch(coroutineContextIO) {
            val allUsers: List<User> = sqlDeLightStorage.getAllUsers()
            state.update {
                it.copy(
                    list = allUsers
                )
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                onOutput(Output.BackButtonClicked)
            }

            Event.OnUserClick -> {
                viewModelScope.launch(coroutineContextIO) {
                    val sessions: List<SessionDTO> = sqlDeLightStorage.getSessions(1)
                    println(sessions.toString())
                }
            }
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}