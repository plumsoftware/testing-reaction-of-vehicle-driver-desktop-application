package presentation.aboutuser.store

sealed class Event {
    data class ChangeSelectedUser(val userId: Long) : Event()

    data object BackButtonClicked : Event()

    data object SaveChanges : Event()
}