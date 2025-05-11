package data

import data.model.Config
import domain.CryptographyRepository
import getConfig
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class CryptographyRepositoryImpl : CryptographyRepository {

    private var config: Config = getConfig()

    private val ALGORITHM = config.cryptography.algorithm
    private val TRANSFORMATION = config.cryptography.transformation

    private val TRANSFORMATION_TEST = "AES/CBC/PKCS5Padding"
    private val KEY_SIZE_TEST = 256

    override suspend fun encode(text: String): String {
        val secretKey = generateSecretKey(key = config.cryptography.secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return encryptedBytes.toHexString()
    }

    override suspend fun decode(text: String): String {
        val secretKey = generateSecretKey(key = config.cryptography.secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(text.hexToByteArray())
        return String(decryptedBytes)
    }

    fun encode(text: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION_TEST)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedText: String, secretKey: SecretKey): String {
        val combined = Base64.getDecoder().decode(encryptedText)
        val encryptedBytes = combined.copyOfRange(16, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION_TEST)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE_TEST)
        return keyGenerator.generateKey()
    }

    fun secretKeyToString(key: SecretKey): String {
        return Base64.getEncoder().encodeToString(key.encoded)
    }

    fun stringToSecretKey(keyString: String): SecretKey {
        val encodedKey = Base64.getDecoder().decode(keyString)
        return javax.crypto.spec.SecretKeySpec(encodedKey, "AES")
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