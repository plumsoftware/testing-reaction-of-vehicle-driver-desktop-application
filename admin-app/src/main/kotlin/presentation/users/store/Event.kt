package presentation.users.store

sealed class Event {
    data object BackClicked : Event()
}