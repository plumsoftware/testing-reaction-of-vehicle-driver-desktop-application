package presentation.settings.store

sealed class Event {
    data object BackClicked : Event()

    data class OnCheckboxThemeChanged(val isChecked: Boolean) : Event()
    data class OnCheckboxXlsxFormatChanged(val isChecked: Boolean) : Event()
    data class OnCheckboxXlsFormatChanged(val isChecked: Boolean) : Event()
    data class OnCheckboxXltxFormatChanged(val isChecked: Boolean) : Event()


    data object SelectLocalFolderToTable : Event()
}