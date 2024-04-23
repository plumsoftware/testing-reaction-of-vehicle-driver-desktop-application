package data.repository

import data.Constants
import domain.model.Settings
import domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun saveData(settings: Settings) {
        println("SettingsRepositoryImpl: User settings to save is: $settings")
//        val jsonString = Json.encodeToString("""{$settings}""".trimIndent())

//        Folder
        val settingsFile = createFolderIfNotExists()
        val stringBuffer = StringBuffer()
        stringBuffer.append("\t\"dataFormats\" : \n\t{\n")
        settings.dataFormats.forEach { (key, value) ->
            if (key != Constants.XLS)
                stringBuffer
                    .append("\t\t\"$key\" : $value,\n")
            else
                stringBuffer
                    .append("\t\t\"$key\" : $value\n")
        }
        stringBuffer.append("\t}")

        val jsonString =
            "{\n" +
                    "\t\"isDarkTheme\" : ${settings.isDarkTheme}," + "\n" +
                    stringBuffer.toString() + "\n" +
                    "}"

        settingsFile.writeText(jsonString)
        println("SettingsRepositoryImpl: json to save: $jsonString")
    }

    override suspend fun loadSettings(): Settings {
        try {
            val settingsFile = File(Constants.PATH_TO_SETTINGS_FILE)
            settingsFile.readText()
        } catch (e: FileNotFoundException) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            createFolderAndFileIfNotExists()
        }

        var settingsFile: File = createFolderIfNotExists()
        var jsonString = settingsFile.readText()
        try {
            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            println("User settings to load is: $settings")
            return settings
        } catch (e: Exception) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            saveData(settings = Settings())

            settingsFile = File(Constants.PATH_TO_SETTINGS_FILE)
            jsonString = settingsFile.readText()
            println("SettingsRepositoryImpl: $jsonString")

            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            return settings
        }
    }

    private fun createFolderIfNotExists(): File {
        val folder = File(Constants.PATH_TO_SETTINGS_FOLDER)
        val settingsFile: File = if (!folder.exists()) {
            folder.mkdir()
            File(Constants.PATH_TO_SETTINGS_FILE)
        } else {
            File(Constants.PATH_TO_SETTINGS_FILE)
        }
        return settingsFile
    }

    private suspend fun createFolderAndFileIfNotExists() {
        val folder = File(Constants.PATH_TO_SETTINGS_FOLDER)
        if (!folder.exists()) {
            folder.mkdir()
            withContext(Dispatchers.IO) {
                File(Constants.PATH_TO_SETTINGS_FILE).createNewFile()
            }
        } else {
            withContext(Dispatchers.IO) {
                File(Constants.PATH_TO_SETTINGS_FILE).createNewFile()
            }
        }
    }
}