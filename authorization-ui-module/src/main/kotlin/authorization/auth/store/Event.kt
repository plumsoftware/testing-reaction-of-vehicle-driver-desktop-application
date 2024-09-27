package authorization.auth.store

sealed class Event {
    data object BackCLicked : Event()

    data class OnNameChanged(val name: String) : Event()
    data class OnSurnameChanged(val surname: String) : Event()
    data class OnPatronymicChanged(val patronymic: String) : Event()

    data object StartTest : Event()
}