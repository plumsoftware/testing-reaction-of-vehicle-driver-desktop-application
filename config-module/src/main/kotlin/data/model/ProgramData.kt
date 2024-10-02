package data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgramData(
    @SerialName("name") val name: String = "",
    @SerialName("version") val version: String = "",
    @SerialName("number") val number: String = "",
)
