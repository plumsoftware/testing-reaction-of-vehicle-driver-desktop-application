package presentation.settings.store

import data.model.regular.settings.Settings
import java.io.File

data class State(
    val isDarkTheme: Boolean = false,
    val isXlsxFormat: Boolean = true,
    val isXlsFormat: Boolean = false,
    val isXltxFormat: Boolean = false,

    val listRoots: List<File> = emptyList(),

    val selectedLocalFolderToTable: File = File("C:\\Users\\Default"),

    val settings: Settings = Settings()
)