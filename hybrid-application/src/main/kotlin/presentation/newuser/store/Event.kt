package presentation.newuser.store

import domain.model.regular.user.Gender

sealed class Event {
    data object BackCLicked : Event()

    data class OnNameChanged(val name: String) : Event()
    data class OnSurnameChanged(val surname: String) : Event()
    data class OnPatronymicChanged(val patronymic: String) : Event()
    data class OnGenderChanged(val gender: Gender) : Event()
    data class OnLoginChanged(val login: String) : Event()
    data class OnPasswordChanged(val password: String) : Event()

    data object SaveNewUser : Event()
}