import data.model.Config
import kotlinx.serialization.json.Json
import java.io.File

inline fun getConfig() : Config {
    val configFile = File("app\\config.json")
    if (!configFile.exists()) {
        throw Exception("Файл конфигурации для ключей не найден. Поместите его в root\\app\\config.json.")
    }
    val jsonString = configFile.readText()
    val config: Config = Json{ignoreUnknownKeys = true}.decodeFromString<Config>(jsonString)
    return config
}