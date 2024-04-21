package domain.model

import androidx.compose.ui.unit.Constraints
import data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Settings(
    @SerialName("isDarkTheme") val isDarkTheme: Boolean = false,
    @SerialName("dataFormats") val dataFormats: Map<String,Boolean> = mapOf(
        Constants.XLSX to true,
        Constants.XLS to false
    )
)
