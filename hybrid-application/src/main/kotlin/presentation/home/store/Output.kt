package presentation.home.store

sealed class Output {
    data object TestsButtonClicked : Output()
    data object SettingsButtonClicked : Output()
    data object AboutProgramButtonClicked : Output()
}