package presentation.settings.store

import domain.model.Settings
import java.io.File

data class State(
    val isDarkTheme: Boolean = false,
    val isXlsxFormat: Boolean = true,
    val isXlsFormat: Boolean = false,

    val listRoots: List<File> = emptyList(),

    val selectedNetworkDrive: File = File("C:\\"),
    val dropdownMenuNetworkDriveExpanded: Boolean = false,

    val selectedLocalDrive: File = File("C:\\"),
    val dropdownMenuLocalDriveExpanded: Boolean = false,

    val settings: Settings = Settings()
)