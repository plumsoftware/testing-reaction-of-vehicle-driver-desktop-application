package presentation.privacy.store

sealed class Event {
    data object BackCLicked : Event()
}