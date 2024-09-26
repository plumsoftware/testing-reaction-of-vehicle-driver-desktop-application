package data

import domain.CryptographyRepository
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class CryptographyRepositoryImpl : CryptographyRepository {
    private val ALGORITHM = "AES"
    private val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    override suspend fun encode(text: String, key: String): String {
        val secretKey = generateSecretKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return encryptedBytes.toHexString()
    }

    override suspend fun decode(text: String, key: String): String {
        val secretKey = generateSecretKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(text.hexToByteArray())
        return String(decryptedBytes)
    }

    private fun generateSecretKey(key: String): SecretKeySpec {
        val keyBytes = key.toByteArray()
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }

    private fun String.hexToByteArray(): ByteArray {
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}

