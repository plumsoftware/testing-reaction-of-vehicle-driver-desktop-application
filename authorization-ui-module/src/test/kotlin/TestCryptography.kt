import data.CryptographyRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestCryptography {

    private val key = "5a61efc456b3417334478c6ab7371641"

    @Test
    fun testSingleEncodeCrypto () = runBlocking {
        val cryptographyRepository = CryptographyRepositoryImpl()
        val encoded = cryptographyRepository.encode(text = "test", key = key)
        assertTrue(true)
    }

    @Test
    fun testSingleDecodeCrypto () = runBlocking {
        val cryptographyRepository = CryptographyRepositoryImpl()
        val encoded = cryptographyRepository.encode(text = "hello world", key = key)
        cryptographyRepository.decode(text = encoded, key = key)
        assertTrue(true)
    }
}