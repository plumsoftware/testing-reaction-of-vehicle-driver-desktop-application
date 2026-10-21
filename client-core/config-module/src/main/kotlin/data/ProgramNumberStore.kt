package data

import java.io.File

/** Уникальный номер установки: создаётся при первом запуске, либо берётся из `app\config.json`. */
internal object ProgramNumberStore {

    private val file = File(ConfigPaths.appDataFolder, "program-number.txt")

    @Synchronized
    fun get(): String {
        val imported = runCatching { readConfigFile() }.getOrNull()?.programData?.number?.takeIf { it.isNotBlank() }
        val stored = file.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.isNotEmpty() }
        val number = imported ?: stored ?: generateUniqueNumber()
        if (number != stored) {
            file.parentFile.mkdirs()
            file.writeText(number)
        }
        return number
    }
}
