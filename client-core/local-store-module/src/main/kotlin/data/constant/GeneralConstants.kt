package data.constant

import java.io.File

object GeneralConstants {
    val USER_NAME: String = System.getProperty("user.name")
    const val FOLDER_NAME = "Reaction test"

    private val OS_NAME: String = System.getProperty("os.name")
    val IS_WINDOWS: Boolean = OS_NAME.startsWith("Windows", ignoreCase = true)
    private val IS_MAC: Boolean = OS_NAME.startsWith("Mac", ignoreCase = true)
    private val USER_HOME: String = System.getProperty("user.home")

    /**
     * Папка с настройками и локальными БД:
     * Windows — `C:\Users\<user>\AppData\Local\Reaction test` (как в прежних версиях, чтобы не потерять данные),
     * macOS — `~/Library/Application Support/Reaction test`, Linux — `~/.local/share/Reaction test`.
     */
    val APP_DATA_FOLDER: File = when {
        IS_WINDOWS -> File("C:\\Users\\$USER_NAME\\AppData\\Local\\$FOLDER_NAME")
        IS_MAC -> File(USER_HOME, "Library/Application Support/$FOLDER_NAME")
        else -> File(System.getenv("XDG_DATA_HOME") ?: "$USER_HOME/.local/share", FOLDER_NAME)
    }

    /** Папка для Excel-файлов по умолчанию: «Документы/Reaction test». */
    val DEFAULT_TABLE_FOLDER: File = File(File(USER_HOME, "Documents"), FOLDER_NAME)

    /** Путь в стиле Windows (`C:\...`), сохранённый в настройках на другой ОС, — там он не работает. */
    fun isForeignWindowsPath(path: String): Boolean = !IS_WINDOWS && Regex("^[A-Za-z]:\\\\").containsMatchIn(path)

    object Paths {
        val PATH_TO_SETTINGS_FOLDER: String = APP_DATA_FOLDER.path
        val PATH_TO_SETTINGS_FILE: String = File(APP_DATA_FOLDER, "settings.json").path

        /** Сетевой режим — только Windows: БД на сетевом диске. */
        fun PATH_TO_ROAMING_DATABASE_DIRECTORY(dir: String) : String{
            return "${dir}Default\\AppData\\Roaming\\${FOLDER_NAME}"
        }
    }
}
