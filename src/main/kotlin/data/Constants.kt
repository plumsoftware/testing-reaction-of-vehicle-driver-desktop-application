package data

class Constants {
    companion object {
        private val USER_NAME: String = System.getProperty("user.name")

        val PATH_TO_SETTINGS_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\Reaction test"
        val PATH_TO_SETTINGS_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Local\\Reaction test\\settings.json"

        const val SHEET_NAME: String = "Данные тестирования"
        const val PATH: String = "C:\\Данные тестирования\\База тестирования.xlsx"
        const val PATH_FOLDER: String = "C:\\Данные тестирования"

        const val XLSX: String = ".xlsx"
        const val XLS: String = ".xls"
    }
}