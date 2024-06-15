package presentation.settings.store

import java.io.File

sealed class Event {
    data object BackClicked : Event()

    data class OnCheckboxThemeChanged(val isChecked: Boolean) : Event()

    data object ExpandDropDownMenuNetworkDrive : Event()

    data object CollapseDropDownMenuNetworkDrive : Event()
    data class SelectDropDownMenuNetworkDriveItem(val item: File) : Event()
}