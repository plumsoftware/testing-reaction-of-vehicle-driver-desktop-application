package domain.model.regular.settings

import data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Settings(
    @SerialName("isDarkTheme") val isDarkTheme: Boolean = false,
    @SerialName("dataFormats") val dataFormats: Map<String,Boolean> = mapOf(
        Constants.Settings.XLSX to true,
        Constants.Settings.XLS to false,
        Constants.Settings.XLTX to false,
    ),

    @SerialName("localFolderToTable") val localFolderToTable: String = Constants.Settings.LOCAL_FOLDER_TO_TABLE
)
