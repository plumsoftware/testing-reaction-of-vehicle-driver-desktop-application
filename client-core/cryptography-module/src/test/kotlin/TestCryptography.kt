import data.CryptographyRepositoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class TestCryptography {

    private val cryptographyRepository = CryptographyRepositoryImpl()

    @Test
    fun `test encodeion and decryption with short text`() {
        val secretKey = cryptographyRepository.generateSecretKey()
        val originalText = "This is a short test."
        val encodeedText = cryptographyRepository.encode(originalText, secretKey)
        val decryptedText = cryptographyRepository.decrypt(encodeedText, secretKey)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `test encodeion and decryption with unicode text`() {
        val secretKey = cryptographyRepository.generateSecretKey()
        val originalText = "Это строка на русском языке."
        val encodeedText = cryptographyRepository.encode(originalText, secretKey)
        val decryptedText = cryptographyRepository.decrypt(encodeedText, secretKey)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `test encodeion and decryption with empty string`() {
        val secretKey = cryptographyRepository.generateSecretKey()
        val originalText = ""
        val encodeedText = cryptographyRepository.encode(originalText, secretKey)
        val decryptedText = cryptographyRepository.decrypt(encodeedText, secretKey)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `test encodeion and decryption with long string`() {
        val secretKey = cryptographyRepository.generateSecretKey()
        val originalText = "This is a very long string used for a test. It should test long text. This is a very long string used for a test. It should test long text. This is a very long string used for a test. It should test long text. This is a very long string used for a test. It should test long text. This is a very long string used for a test. It should test long text."
        val encodeedText = cryptographyRepository.encode(originalText, secretKey)
        val decryptedText = cryptographyRepository.decrypt(encodeedText, secretKey)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `test encodeion and decryption with number secret key`() {
        val secretKeyString = "12345678901234567890123456789012"
        val secretKey = cryptographyRepository.stringToSecretKey(secretKeyString)
        val originalText = "This is a test with a number secret key."
        val encodedText = cryptographyRepository.encode(originalText, secretKey)
        val decryptedText = cryptographyRepository.decrypt(encodedText, secretKey)
        assertEquals(originalText, decryptedText)
    }
}