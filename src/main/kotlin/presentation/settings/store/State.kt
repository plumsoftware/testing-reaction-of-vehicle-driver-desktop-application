package presentation.settings.store

import domain.model.Settings
import java.io.File

data class State(
    val isDarkTheme: Boolean = false,
    val isXlsxFormat: Boolean = true,
    val isXlsFormat: Boolean = false,

    val listRoots: List<File> = emptyList(),
    val selectedNetworkDrive: File = listRoots[0],
    val dropdownMenuExpanded: Boolean = false,

    val settings: Settings = Settings()
)