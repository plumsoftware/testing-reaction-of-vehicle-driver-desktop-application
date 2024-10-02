package about_app.store

sealed class Event {
    data object BackClick : Event()
}