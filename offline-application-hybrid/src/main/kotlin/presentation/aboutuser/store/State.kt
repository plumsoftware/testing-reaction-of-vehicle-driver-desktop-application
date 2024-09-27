package presentation.aboutuser.store

import data.model.dto.database.SessionDTO
import data.model.either.AppEither
import data.model.regular.user.User

data class State(
    val user: User = User.empty(),
    val sessions: List<SessionDTO> = emptyList(),
    val filteredSessionsList: List<SessionDTO> = emptyList(),
    val selectedChipList: MutableList<Boolean> = mutableListOf(true, false, false, false),

    val login: String = "",
    val password: String = "",


    val isPasswordError: Boolean = false,
    val isLoginError: Boolean = false,
    val isFilterError: Boolean = false,
    val isNameError: Boolean = false,
    val isSurnameError: Boolean = false,

    val testNumberFilter: String = "",

    val appEither: AppEither = AppEither.Handle
)
