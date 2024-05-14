package presentation.authorization.login.store

import domain.model.User

sealed class Output {
    data object BackButtonClicked : Output()
    data class OpenTestMenu(val user: User, val count: Int) : Output()
}