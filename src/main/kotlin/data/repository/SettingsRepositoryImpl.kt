package data.repository

import domain.model.Settings
import domain.repository.SettingsRepository
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun saveData(settings: Settings) {
        println("SettingsRepositoryImpl: User settings to save is: $settings")
        val settingsFile = File("settings.json")
//        val jsonString = Json.encodeToString("""{$settings}""".trimIndent())
        val jsonString = "{\n" +
                "\t\"isDarkTheme\" : ${settings.isDarkTheme}" + "\n" +
                "}"
        settingsFile.writeText(jsonString)
        println("SettingsRepositoryImpl: json to save: $jsonString")
    }

    override suspend fun loadSettings(): Settings {
        try {
            val settingsFile = File("settings.json")
            settingsFile.readText()
        } catch (e: FileNotFoundException) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            File("settings.json").createNewFile()
        }

        var settingsFile = File("settings.json")
        var jsonString = settingsFile.readText()
        try {
            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            println("User settings to load is: $settings")
            return settings
        } catch (e: Exception) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            saveData(settings = Settings())

            settingsFile = File("settings.json")
            jsonString = settingsFile.readText()
            println("SettingsRepositoryImpl: $jsonString")

            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            return settings
        }
    }
}