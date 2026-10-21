package presentation.settings.store

import data.model.regular.settings.Settings
import data.constant.SettingsConstants
import java.io.File

data class State(
    val isDarkTheme: Boolean = false,
    val isXlsxFormat: Boolean = true,
    val isXlsFormat: Boolean = false,
    val isXltxFormat: Boolean = false,

    val listRoots: List<File> = emptyList(),

    val selectedLocalFolderToTable: File = File(SettingsConstants.LOCAL_FOLDER_TO_TABLE),

    val settings: Settings = Settings(),

    /** Итог последнего экспорта ключа. */
    val keyExportMessage: String = ""
)