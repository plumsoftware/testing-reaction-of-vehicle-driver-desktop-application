package presentation.users.store

import data.model.regular.user.User

sealed class Event {
    data object BackClicked : Event()
    data class OnUserClick(val user: User) : Event()
    data class OnSearch(val query: String) : Event()
    data object InitUsers : Event()
}