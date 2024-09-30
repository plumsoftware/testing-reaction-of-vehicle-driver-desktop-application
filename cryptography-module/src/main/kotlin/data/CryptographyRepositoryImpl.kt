package data

import data.model.Config
import domain.CryptographyRepository
import getConfig
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class CryptographyRepositoryImpl : CryptographyRepository {

    private var config: Config = getConfig()

    private val ALGORITHM = "AES"
    private val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    override suspend fun encode(text: String): String {
        val secretKey = generateSecretKey(key = config.secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return encryptedBytes.toHexString()
    }

    override suspend fun decode(text: String): String {
        val secretKey = generateSecretKey(key = config.secretKey)
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