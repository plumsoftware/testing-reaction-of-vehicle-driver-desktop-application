package data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cryptography(
    @SerialName("secret_key") val secretKey: String = "",
    @SerialName("algorithm") val algorithm: String = "",
    @SerialName("transformation") val transformation: String = ""
)
