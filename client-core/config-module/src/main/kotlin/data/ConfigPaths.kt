package data

import java.io.File

internal object ConfigPaths {
    private const val FOLDER_NAME = "Reaction test"

    private val osName: String = System.getProperty("os.name")
    private val userHome: String = System.getProperty("user.home")

    /**
     * Папка данных программы, та же, где лежат локальные БД (см. GeneralConstants.APP_DATA_FOLDER в local-store-module):
     * Windows — `C:\Users\<user>\AppData\Local\Reaction test`, macOS — `~/Library/Application Support/Reaction test`,
     * Linux — `~/.local/share/Reaction test`.
     */
    val appDataFolder: File = when {
        osName.startsWith("Windows", ignoreCase = true) ->
            File("C:\\Users\\${System.getProperty("user.name")}\\AppData\\Local\\$FOLDER_NAME")
        osName.startsWith("Mac", ignoreCase = true) -> File(userHome, "Library/Application Support/$FOLDER_NAME")
        else -> File(System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share", FOLDER_NAME)
    }

    /** Файл для переноса ключа между компьютерами, относительно рабочей директории. */
    val importFile: File = File("app", "config.json")
}
