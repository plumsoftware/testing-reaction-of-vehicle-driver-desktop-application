package presentation.aboutuser.viewmodel

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.User
import domain.storage.SQLDeLightStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import presentation.aboutuser.store.Event
import presentation.aboutuser.store.Output
import presentation.aboutuser.store.State
import kotlin.coroutines.CoroutineContext

class AboutUserViewModel(
    private val sqlDeLightStorage: SQLDeLightStorage,
    private val output: (Output) -> Unit,
    private val coroutineContextIO: CoroutineContext
) : ViewModel() {

    val state = MutableStateFlow(State())

    init {
        println("About user page created")
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.ChangeSelectedUser -> {
                viewModelScope.launch(coroutineContextIO) {
                    val sessions: List<SessionDTO> = sqlDeLightStorage.getSessions(userId = event.userId)
                    val user = sqlDeLightStorage.getAllUsers().find { it.id == event.userId }
                        ?: User.empty() //TODO(replace later)

                    state.update {
                        it.copy(user = user, sessions = sessions)
                    }
                }
            }

            Event.BackButtonClicked -> {
                onOutput(Output.BackButtonClicked)
            }

            Event.SaveChanges -> {

            }
        }
    }

    private fun onOutput(o: Output) {
        output(o)
    }
}