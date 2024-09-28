package presentation.users.viewmodel

import data.model.regular.user.User
import domain.storage.UserStorage
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.users.store.Effect
import presentation.users.store.Event
import presentation.users.store.State

class UsersViewModel(
    private val userStorage: UserStorage,
) : ViewModel() {

    val state = MutableStateFlow(State())
    val effect = MutableSharedFlow<Effect>()

    private val supervisorCoroutineContext = viewModelScope.coroutineContext + SupervisorJob()

    init {
        println("Users view model created")
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.BackClicked -> {
                viewModelScope.launch {
                    effect.emit(Effect.BackCLicked)
                }
            }

            is Event.OnUserClick -> {
                viewModelScope.launch {
                    effect.emit(Effect.UserCLicked(selectedUser = event.user))
                }
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

            Event.InitUsers -> {
                viewModelScope.launch(context = supervisorCoroutineContext) {
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
    }
}