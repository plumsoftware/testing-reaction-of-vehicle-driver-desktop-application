package data

import data.model.Cryptography
import domain.CryptographyRepository
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @param canGenerateKey вызывается, если ключа ещё нет: true разрешает создать новый
 * (только когда в базах нет данных, зашифрованных прежним ключом).
 */
class CryptographyRepositoryImpl(
    scope: KeyScope = KeyScope.LOCAL,
    canGenerateKey: () -> Boolean = { false },
) : CryptographyRepository {

    private val cryptography: Cryptography = CryptographyKeyStore.load(scope = scope, canGenerate = canGenerateKey)

    private val ALGORITHM = cryptography.algorithm
    private val TRANSFORMATION = cryptography.transformation

    override suspend fun encode(text: String): String {
        val secretKey = generateSecretKey(key = cryptography.secretKey)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return encryptedBytes.toHexString()
    }

    override suspend fun decode(text: String): String {
        val secretKey = generateSecretKey(key = cryptography.secretKey)
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