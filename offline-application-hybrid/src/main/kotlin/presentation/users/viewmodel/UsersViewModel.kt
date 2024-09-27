package presentation.users.viewmodel

import data.model.regular.user.User
import domain.storage.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel
import presentation.users.store.Action
import presentation.users.store.Event
import presentation.users.store.Output
import presentation.users.store.State

class UsersViewModel(
    private val output: (Output) -> Unit,
    private val userStorage: UserStorage,
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("Users view model created")
    }

    suspend fun onAction(action: Action) {
        when (action) {
            Action.InitUsers -> {
                val allUsers: List<User> = userStorage.getAllUsers()
                state.update {
                    it.copy(
                        list = allUsers,
                        searchList = allUsers
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

            is Event.OnSearch -> {
                state.update {
                    it.copy(
                        query = event.query
                    )
                }

                val allUsers: List<User> = state.value.list

                if (state.value.query.isNotEmpty()) {
                    val searchList: MutableList<User> = mutableListOf()
                    allUsers.forEachIndexed { _, user ->
                        if (user.name.lowercase().contains(state.value.query.lowercase()) ||
                            user.surname.lowercase().contains(state.value.query.lowercase()) ||
                            user.patronymic.lowercase().contains(state.value.query.lowercase())
                        ) {
                            searchList.add(user)
                        }
                    }
                    state.update {
                        it.copy(
                            searchList = searchList
                        )
                    }
                } else {
                    state.update {
                        it.copy(
                            searchList = allUsers
                        )
                    }
                }
            }
        }
    }

    fun onOutput(o: Output) {
        output(o)
    }
}