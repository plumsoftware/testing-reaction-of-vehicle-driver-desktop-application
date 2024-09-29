package data.model

import java.security.SecureRandom

class Configurator {
    companion object {
        fun getKey() : String {
            return "secret_key"
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun generateRandomKey(keySize: Int): String {
        val random = SecureRandom()
        val keyBytes = ByteArray(keySize / 8)
        random.nextBytes(keyBytes)
        return keyBytes.toHexString()
    }
}