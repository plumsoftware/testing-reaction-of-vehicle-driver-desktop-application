package data.repository

import data.Constants
import domain.model.regular.settings.Settings
import domain.repository.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException

class SettingsRepositoryImpl : SettingsRepository {

    init {
        createFolderIfNotExists()
    }

    override fun saveData(settings: Settings) {
        println("SettingsRepositoryImpl: User settings to save is: $settings")
//        val jsonString = Json.encodeToString("""{$settings}""".trimIndent())

//        Folder
        val settingsFile = createFolderIfNotExists()

        val jsonString =
            "{\n" +
                    "\t\"isDarkTheme\" : ${settings.isDarkTheme}," + "\n" +
                    "\t\"networkDrive\" : \"${settings.networkDrive}\"" + "\n" +
                    "}"

        settingsFile.writeText(jsonString)
        println("SettingsRepositoryImpl: json to save: $jsonString")
    }

    override fun loadSettings(scope: CoroutineScope): Settings {
        try {
            val settingsFile = File(Constants.General.PATH_TO_SETTINGS_FILE)
            settingsFile.readText()
        } catch (e: FileNotFoundException) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            runBlocking {
                createFolderAndFileIfNotExists()
            }
        }

        var settingsFile = File(Constants.General.PATH_TO_SETTINGS_FILE)
        var jsonString = settingsFile.readText()

        if (jsonString.isNotEmpty()) {
            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            println("User settings to load is: $settings")
            return settings
        } else {
            println("User setting is empty ")
            saveData(settings = Settings())
            settingsFile = File(Constants.General.PATH_TO_SETTINGS_FILE)
            jsonString = settingsFile.readText()
            println("SettingsRepositoryImpl: string from file: $jsonString")

            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            return settings
        }
    }

    private fun createFolderIfNotExists(): File {
        val folder = File(Constants.General.PATH_TO_SETTINGS_FOLDER)
        val settingsFile: File = if (!folder.exists()) {
            folder.mkdir()
            File(Constants.General.PATH_TO_SETTINGS_FILE)
        } else {
            File(Constants.General.PATH_TO_SETTINGS_FILE)
        }
        return settingsFile
    }

    private suspend fun createFolderAndFileIfNotExists() {
        val folder = File(Constants.General.PATH_TO_SETTINGS_FOLDER)
        if (!folder.exists()) {
            folder.mkdir()
            withContext(Dispatchers.IO) {
                File(Constants.General.PATH_TO_SETTINGS_FILE).createNewFile()
            }
        } else {
            withContext(Dispatchers.IO) {
                File(Constants.General.PATH_TO_SETTINGS_FILE).createNewFile()
            }
        }
    }
}