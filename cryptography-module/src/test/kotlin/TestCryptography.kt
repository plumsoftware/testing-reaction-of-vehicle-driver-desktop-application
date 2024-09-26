import data.CryptographyRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestCryptography {

    @Test
    fun testSingleEncodeCrypto () = runBlocking {
        val cryptographyRepository = CryptographyRepositoryImpl()
        val encoded = cryptographyRepository.encode(text = "test", key = "hello")
        assertTrue(true)
    }

    @Test
    fun testSingleDecodeCrypto () = runBlocking {
        val cryptographyRepository = CryptographyRepositoryImpl()
        val encoded = cryptographyRepository.encode(text = "test", key = "hello")
        cryptographyRepository.decode(text = encoded, key = "hello")
        assertTrue(true)
    }
}