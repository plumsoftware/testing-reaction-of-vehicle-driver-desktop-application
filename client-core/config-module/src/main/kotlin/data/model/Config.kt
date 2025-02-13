package data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("cryptography") val cryptography: Cryptography = Cryptography(),
    @SerialName("program_data") val programData: ProgramData = ProgramData()
)