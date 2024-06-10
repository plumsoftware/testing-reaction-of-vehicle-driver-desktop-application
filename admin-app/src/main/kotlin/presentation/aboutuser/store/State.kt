package presentation.aboutuser.store

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.User

data class State(
    val user: User = User.empty(),
    val sessions: List<SessionDTO> = emptyList(),

    val login: String = "",
    val password: String = "",


    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
)
