package domain.model.regular.settings

import data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Settings(
    @SerialName("isDarkTheme") val isDarkTheme: Boolean = false,
    @SerialName("networkDrive") val networkDrive: String = Constants.Settings.NETWORK_DRIVE,
)
