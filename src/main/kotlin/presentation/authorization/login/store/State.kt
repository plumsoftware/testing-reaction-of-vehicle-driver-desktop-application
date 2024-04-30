package presentation.authorization.login.store

data class State (
    val login: String = "",
    val password: String = ""
)