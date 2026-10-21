package data

import com.sun.jna.platform.win32.Crypt32Util
import data.model.Config
import data.model.Cryptography
import data.model.ProgramData
import kotlinx.serialization.json.Json
import java.io.File

/** Ключа шифрования нет (или он нечитаем), а без него данные в базе не расшифровать. */
class MissingCryptographyKeyException(message: String, cause: Throwable? = null) : IllegalStateException(message, cause)

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
            throw MissingCryptographyKeyException(
                "На этом компьютере нет ключа шифрования, а в базе уже есть пользователи. " +
                    "Получите файл config.json у администратора (программа администратора или гибрид: " +
                    "Настройки → «Экспорт ключа») и положите его в папку ${importFolderPath()}, затем нажмите «Повторить»."
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

    /**
     * Сохраняет ключ [scope] и номер программы в [target] в формате config.json — для переноса
     * на другие компьютеры (сетевой режим) или резервной копии.
     */
    fun export(scope: KeyScope, target: File) {
        val cryptography = readStored(scope)
            ?: throw MissingCryptographyKeyException(
                "Ключ ещё не создан: он появляется при первом открытии базы. Откройте базу (например, список пользователей) и повторите экспорт."
            )
        val config = Config(cryptography = cryptography, programData = ProgramData(number = ProgramNumberStore.get()))
        target.parentFile?.mkdirs()
        target.writeText(exportJson.encodeToString(Config.serializer(), config))
    }

    /** Папка, из которой программа берёт config.json при запуске. */
    fun importFolderPath(): String = ConfigPaths.importFile.absoluteFile.parentFile.path

    private val exportJson = Json { prettyPrint = true }

    private fun file(scope: KeyScope) = File(ConfigPaths.appDataFolder, scope.fileName)

    private fun readStored(scope: KeyScope): Cryptography? {
        val file = file(scope)
        if (!file.exists()) return null
        val bytes = try {
            unprotect(file.readBytes())
        } catch (e: Exception) {
            // Не создаём новый ключ поверх: старые данные стали бы нечитаемыми навсегда.
            throw MissingCryptographyKeyException(
                "Не удалось прочитать ключ шифрования ${file.absolutePath}: он сохранён другой учётной записью Windows. " +
                    "Положите config.json с этим ключом в папку ${importFolderPath()}, затем нажмите «Повторить».",
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
