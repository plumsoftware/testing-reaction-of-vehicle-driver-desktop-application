package data.model.regular.settings

import data.constant.SettingsConstants
import data.constant.ExcelConstants
import java.io.File
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Settings(
    @SerialName("isDarkTheme") val isDarkTheme: Boolean = false,
    @SerialName("dataFormats") val dataFormats: Map<String,Boolean> = mapOf(
        SettingsConstants.XLSX to true,
        SettingsConstants.XLS to false,
        SettingsConstants.XLTX to false,
    ),
    @SerialName("networkDrive") val networkDrive: String = SettingsConstants.NETWORK_DRIVE,
    @SerialName("localFolderToTable") val localFolderToTable: String = SettingsConstants.LOCAL_FOLDER_TO_TABLE
)

/** Excel-файлы, в которые записываются результаты (по включённым форматам). */
val Settings.workbookFiles: List<File>
    get() = dataFormats.filterValues { it }.keys.map { ExcelConstants.workbookFile(localFolderToTable, it) }
