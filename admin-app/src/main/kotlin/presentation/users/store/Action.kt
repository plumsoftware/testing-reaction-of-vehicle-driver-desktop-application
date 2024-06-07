package presentation.users.store

sealed class Action {
    data object InitUsers : Action()
}