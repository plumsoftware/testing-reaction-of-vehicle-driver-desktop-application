package data

import java.security.SecureRandom

@OptIn(ExperimentalStdlibApi::class)
private fun generateRandomKey(keySize: Int): String {
    val random = SecureRandom()
    val keyBytes = ByteArray(keySize / 8)
    random.nextBytes(keyBytes)
    return keyBytes.toHexString()
}