package domain

class CryptographyStorage(private val cryptographyRepository: CryptographyRepository) {

    suspend fun encode(text: String, key: String) : String {
        return cryptographyRepository.encode(text = text, key = key)
    }

    suspend fun decode(encoded: String, key: String) : String {
        return cryptographyRepository.decode(text = encoded, key = key)
    }
}