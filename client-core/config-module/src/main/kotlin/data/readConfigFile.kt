package data

import data.model.Config
import kotlinx.serialization.json.Json

internal val configJson = Json { ignoreUnknownKeys = true }

/** Читает `app\config.json`, если он есть. Файл нужен только для переноса ключа на другой компьютер. */
internal fun readConfigFile(): Config? {
    val file = ConfigPaths.importFile
    if (!file.exists()) return null
    return configJson.decodeFromString<Config>(file.readText())
}
