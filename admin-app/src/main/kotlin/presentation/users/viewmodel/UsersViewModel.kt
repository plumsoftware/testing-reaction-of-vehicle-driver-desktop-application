package presentation.users.viewmodel

import domain.model.regular.user.User
import domain.storage.SQLDeLightStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.users.store.Action
import presentation.users.store.Event
import presentation.users.store.Output
import presentation.users.store.State

class UsersViewModel(
    private val output: (Output) -> Unit,
    private val sqlDeLightStorage: SQLDeLightStorage,
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Users view model created")
    }

    suspend fun onAction(action: Action) {
        when(action) {
            Action.InitUsers -> {
                val allUsers: List<User> = sqlDeLightStorage.getAllUsers()
                state.update {
                    it.copy(
                        list = allUsers
                    )
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                onOutput(Output.BackButtonClicked)
            }

            is Event.OnUserClick -> {
                onOutput(Output.OnUserClicked(userId = event.userId))
            }
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}