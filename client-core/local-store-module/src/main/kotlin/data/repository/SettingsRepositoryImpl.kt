package data.repository

import data.constant.GeneralConstants
import data.constant.SettingsConstants
import data.model.regular.settings.Settings
import domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import utlis.createFolderIfNotExists
import java.io.File
import java.io.FileNotFoundException

class SettingsRepositoryImpl : SettingsRepository {

    init {
        createFolderIfNotExists(directoryPath = GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER)
    }

    override fun saveData(settings: Settings) {
        println("SettingsRepositoryImpl: User settings to save is: $settings")
        // Сериализатор сам экранирует кавычки и обратные слэши в путях
        createFolderIfNotExists().writeText(settingsJson.encodeToString(Settings.serializer(), settings))
    }

    private val settingsJson = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    override fun loadSettings(scope: CoroutineScope): Settings {
        try {
            val settingsFile = File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE)
            settingsFile.readText()
        } catch (e: FileNotFoundException) {
            println("SettingsRepositoryImpl: Ошибка в settingsViewModel: " + e.message)
            runBlocking {
                createFolderAndFileIfNotExists()
            }
        }

        var settingsFile = File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE)
        var jsonString = settingsFile.readText()

        if (jsonString.isNotEmpty()) {
            var settings: Settings = Json { ignoreUnknownKeys = true }.decodeFromString<Settings>(jsonString)
            // Настройки, перенесённые с Windows (или записанные старой версией на macOS/Linux)
            if (GeneralConstants.isForeignWindowsPath(settings.localFolderToTable)) {
                settings = settings.copy(localFolderToTable = SettingsConstants.LOCAL_FOLDER_TO_TABLE)
                saveData(settings = settings)
            }
            println("User settings to load is: $settings")
            return settings
        } else {
            println("User setting is empty ")
            saveData(settings = Settings())
            settingsFile = File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE)
            jsonString = settingsFile.readText()
            println("SettingsRepositoryImpl: string from file: $jsonString")

            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
            return settings
        }
    }

    private fun createFolderIfNotExists(): File {
        val folder = File(GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER)
        val settingsFile: File = if (!folder.exists()) {
            folder.mkdir()
            File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE)
        } else {
            File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE)
        }
        return settingsFile
    }

    private suspend fun createFolderAndFileIfNotExists() {
        val folder = File(GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        withContext(Dispatchers.IO) {
            File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE).createNewFile()
        }
    }
}