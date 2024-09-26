package domain

interface CryptographyRepository {
    suspend fun encode(text: String, key: String): String
    suspend fun decode(text: String, key: String): String
}