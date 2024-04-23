package data

object Constants {
    object General {
        private val USER_NAME: String = System.getProperty("user.name")
        private const val FOLDER_NAME = "Reaction test"

        val PATH_TO_SETTINGS_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PATH_TO_SETTINGS_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}\\settings.json"

        val PATH_TO_SHARED_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Roaming\\${FOLDER_NAME}"
        val PATH_TO_SHARED_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Roaming\\${FOLDER_NAME}\\shared.json"
    }

    object Table {
        const val SHEET_NAME: String = "Данные тестирования"
        const val PATH: String = "C:\\Данные тестирования\\База тестирования.xlsx"
        const val PATH_FOLDER: String = "C:\\Данные тестирования"
    }

    object Settings {
        const val XLSX: String = ".xlsx"
        const val XLS: String = ".xls"
        const val NETWORK_DRIVE: String = ""
        val ADMIN: String = System.getProperty("user.name")
    }
}