package presentation.home.store

sealed class Label {
    data object TestsButtonClicked : Label()
    data object SettingsButtonClicked : Label()
    data object AboutProgramButtonClicked : Label()
}