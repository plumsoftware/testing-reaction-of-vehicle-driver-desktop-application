package presentation.settings.store

import java.io.File

sealed class Event {
    data object BackClicked : Event()

    data class OnCheckboxThemeChanged(val isChecked: Boolean) : Event()
    data class OnCheckboxXlsxFormatChanged(val isChecked: Boolean) : Event()
    data class OnCheckboxXlsFormatChanged(val isChecked: Boolean) : Event()

    data object ExpandDropDownMenu : Event()

    data object CollapseDropDownMenu : Event()
    data class SelectDropDownMenuItem(val item: File) : Event()
}