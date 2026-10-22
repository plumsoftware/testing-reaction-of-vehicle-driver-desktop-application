import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA512

class HashRepositoryImpl : HashRepository {
    override suspend fun hash(text: String): String {
        return CryptographyProvider.Default
            .get(SHA512)
            .hasher()
            .hash(text.encodeToByteArray()).decodeToString()
    }
}