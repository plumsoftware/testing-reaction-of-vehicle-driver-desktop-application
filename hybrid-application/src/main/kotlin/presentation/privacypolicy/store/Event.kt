package presentation.privacypolicy.store

sealed class Event {
    data object BackCLicked : Event()
}