package privacypolicy.store

sealed class Event {
    data object BackCLicked : Event()
}