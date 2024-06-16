package presentation.home.store

sealed class Event {
    data object TestsButtonClicked : Event()
    data object SettingsButtonClicked : Event()
    data object AboutProgramButtonClicked : Event()
    data object UsersButtonClicked : Event()
    data object AddNewUserButtonClicked : Event()
}