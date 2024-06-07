package presentation.users.store

sealed class Event {
    data object BackClicked : Event()

    data class OnUserClick(val userId: Long) : Event()
}