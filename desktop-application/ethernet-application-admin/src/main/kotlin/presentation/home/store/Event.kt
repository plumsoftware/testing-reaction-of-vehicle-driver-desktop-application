package presentation.home.store

sealed class Event {
    data object UsersButtonClicked : Event()
    data object AddNewUserButtonClicked : Event()
    data object SettingsButtonClicked : Event()
    data object AboutProgramButtonClicked : Event()
}