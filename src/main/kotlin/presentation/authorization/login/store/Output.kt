package presentation.authorization.login.store

sealed class Output {
    data object BackButtonClicked : Output()
    data object OpenTestMenu : Output()
}