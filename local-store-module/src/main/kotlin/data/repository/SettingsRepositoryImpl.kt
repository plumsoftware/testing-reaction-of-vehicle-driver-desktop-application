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
//        val jsonString = Json.encodeToString("""{$settings}""".trimIndent())

//        Folder
        val settingsFile = createFolderIfNotExists()
        val stringBuffer = StringBuffer()
        stringBuffer.append("\t\"dataFormats\" : \n\t{\n")
        settings.dataFormats.forEach { (key, value) ->
            if (key != SettingsConstants.XLTX)
                stringBuffer
                    .append("\t\t\"$key\" : $value,\n")
            else
                stringBuffer
                    .append("\t\t\"$key\" : $value\n")
        }
        stringBuffer.append("\t},\n")

//        Local folder to table
        stringBuffer
            .append(
                "\t\"localFolderToTable\" : \"${
                    settings.localFolderToTable.replace(
                        "\\",
                        "\\\\"
                    )
                }\""
            )

        val jsonString =
            "{\n" +
                    "\t\"isDarkTheme\" : ${settings.isDarkTheme}," + "\n" +
                    stringBuffer.toString() + "\n" +
                    "}"

        settingsFile.writeText(jsonString)
        println("SettingsRepositoryImpl: json to save: $jsonString")
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
            val settings: Settings = Json.decodeFromString<Settings>(jsonString)
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
            folder.mkdir()
        }
        withContext(Dispatchers.IO) {
            File(GeneralConstants.Paths.PATH_TO_SETTINGS_FILE).createNewFile()
        }
    }
}