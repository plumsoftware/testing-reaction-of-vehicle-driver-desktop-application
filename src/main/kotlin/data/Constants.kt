package data

import org.apache.poi.xssf.usermodel.XSSFWorkbookType

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
        const val FILE_NAME: String = "База тестирования"

        const val TABLE_NAME_XLSX: String = "База тестирования.xlsx"
        const val TABLE_NAME_XLSM: String = "База тестирования.xlsm"

        const val PATH_FOLDER: String = "C:\\Данные тестирования"
    }

    object Settings {
        const val XLSX: String = ".xlsx"
        const val XLSM: String = ".xlsm"
        const val NETWORK_DRIVE: String = ""
        const val LOCAL_DRIVE: String = ""
        const val LOCAL_FOLDER_TO_TABLE: String = "C:\\Users\\Default"
        val ADMIN: String = System.getProperty("user.name")

        val FORMAT_LIST = mapOf<String, XSSFWorkbookType>(
            ".xlsx" to XSSFWorkbookType.XLSX,
            ".xlsm" to XSSFWorkbookType.XLSM,
        )
    }
}