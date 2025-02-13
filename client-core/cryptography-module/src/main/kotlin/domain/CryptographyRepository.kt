package domain

interface CryptographyRepository {
    suspend fun encode(text: String): String
    suspend fun decode(text: String): String
}