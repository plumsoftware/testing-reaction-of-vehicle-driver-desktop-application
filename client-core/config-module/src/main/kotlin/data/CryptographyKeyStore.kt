package data

import com.sun.jna.platform.win32.Crypt32Util
import data.model.Cryptography
import java.io.File

/**
 * Хранит параметры шифрования персональных данных в `%LOCALAPPDATA%\Reaction test`.
 * На Windows файл защищён DPAPI (расшифровать его может только та же учётная запись Windows),
 * на других ОС лежит открытым текстом — они используются только для разработки.
 */
object CryptographyKeyStore {

    private const val KEY_SIZE_BITS = 128
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    private val isWindows = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)

    /**
     * Порядок поиска ключа:
     * 1. `app\config.json` — если файл положили рядом с программой, он главнее и сохраняется в хранилище;
     * 2. ранее сохранённый ключ;
     * 3. новый ключ — только если [canGenerate] подтверждает, что в базах ещё нет зашифрованных данных.
     */
    @Synchronized
    fun load(scope: KeyScope, canGenerate: () -> Boolean): Cryptography {
        val imported = readConfigFile()?.cryptography?.takeIf { it.secretKey.isNotBlank() }
        if (imported != null) {
            if (imported != readStored(scope)) save(scope, imported)
            return imported
        }

        readStored(scope)?.let { return it }

        if (!canGenerate()) {
            throw IllegalStateException(
                "Ключ шифрования не найден, а в базе уже есть пользователи. " +
                    "Скопируйте app\\config.json с компьютера, где программа уже работает, и положите его рядом с программой."
            )
        }

        val generated = Cryptography(
            secretKey = generateSecretKey(keySize = KEY_SIZE_BITS),
            algorithm = ALGORITHM,
            transformation = TRANSFORMATION,
        )
        save(scope, generated)
        return generated
    }

    private fun file(scope: KeyScope) = File(ConfigPaths.appDataFolder, scope.fileName)

    private fun readStored(scope: KeyScope): Cryptography? {
        val file = file(scope)
        if (!file.exists()) return null
        val bytes = try {
            unprotect(file.readBytes())
        } catch (e: Exception) {
            // Не создаём новый ключ поверх: старые данные стали бы нечитаемыми навсегда.
            throw IllegalStateException(
                "Не удалось прочитать ключ шифрования ${file.absolutePath}: он сохранён другой учётной записью Windows. " +
                    "Положите рядом с программой app\\config.json с этим ключом.",
                e
            )
        }
        return configJson.decodeFromString<Cryptography>(bytes.decodeToString())
    }

    private fun save(scope: KeyScope, cryptography: Cryptography) {
        val file = file(scope)
        file.parentFile.mkdirs()
        file.writeBytes(protect(configJson.encodeToString(Cryptography.serializer(), cryptography).encodeToByteArray()))
    }

    private fun protect(bytes: ByteArray): ByteArray = if (isWindows) Crypt32Util.cryptProtectData(bytes) else bytes

    private fun unprotect(bytes: ByteArray): ByteArray = if (isWindows) Crypt32Util.cryptUnprotectData(bytes) else bytes
}
