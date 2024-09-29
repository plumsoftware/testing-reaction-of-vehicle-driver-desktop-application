package presentation.settings.store

import data.model.regular.settings.Settings
import java.io.File

data class State(
    val isDarkTheme: Boolean = false,

    val listRoots: List<File> = emptyList(),

    val selectedNetworkDrive: File = File("C:\\"),
    val dropdownMenuNetworkDriveExpanded: Boolean = false,

    val settings: Settings = Settings()
)