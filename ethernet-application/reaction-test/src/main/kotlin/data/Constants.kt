package data

import domain.model.regular.user.Interval
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

object Constants {
    private val USER_NAME: String = System.getProperty("user.name")
    const val FOLDER_NAME = "Reaction test"
    private const val ROAMING_FOLDER_NAME = "Admin - Reaction test"

    object General {
        val PATH_TO_SETTINGS_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PATH_TO_SETTINGS_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}\\settings.json"

        val PATH_TO_SHARED_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Roaming\\${FOLDER_NAME}"
        val PATH_TO_SHARED_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Roaming\\${FOLDER_NAME}\\shared.json"

        val PATH_TO_LOCAL_SQL_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PART_PATH_TO_LOCAL_SQL_FOLDER = "C:\\Users\\${USER_NAME}"
        val PATH_TO_ROAMING_SQL_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Roaming\\${FOLDER_NAME}"
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
        const val XLSX: String = ".xlsx" //Used
        const val XLSM: String = ".xlsm"
        const val XLTX: String = ".xltx" //Used
        const val XLS: String = ".xls" //Used

        const val NETWORK_DRIVE: String = ""
        const val LOCAL_DRIVE: String = ""
        const val LOCAL_FOLDER_TO_TABLE: String = "C:\\Users\\Default"
        val ADMIN: String = System.getProperty("user.name")

        val FORMAT_LIST = mapOf<String, XSSFWorkbookType>(
            ".xlsx" to XSSFWorkbookType.XLSX,
            ".xls" to XSSFWorkbookType.XLSX,
            ".xltx" to XSSFWorkbookType.XLSX,
        )
    }

    object Test {
        val counts = arrayOf(1, 10, 50, 100, 150, 200, 250, 300)
        val intervals: Array<Interval> = arrayOf(
            Interval(start = 1000, finish = 2000),
            Interval(start = 2000, finish = 5000),
            Interval(start = 2000, finish = 10000),
        )
    }

    object Database {
        private const val LOCAL_DATABASE_NAME = "sessions_database.db"
        val LOCAL_JDBC_DRIVER_NAME = "jdbc:sqlite:C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}\\${LOCAL_DATABASE_NAME}"

        private const val ROAMING_DATABASE_NAME = "user_database.db"
        private const val ROAMING_SESSIONS_DATABASE_NAME = "sessions_database.db"

        fun collapseNetDriver(netDriver: String) : String {
            val driver = "${netDriver.split(":")[0]}:\\"
            return "jdbc:sqlite:${driver}\\AppData\\Roaming\\${ROAMING_FOLDER_NAME}\\${ROAMING_DATABASE_NAME}"
        }

        fun collapseNetDriverToSessions(netDriver: String) : String {
            val driver = "${netDriver.split(":")[0]}:\\"
            return "jdbc:sqlite:${driver}\\AppData\\Roaming\\${ROAMING_FOLDER_NAME}\\${ROAMING_SESSIONS_DATABASE_NAME}"
        }
    }
}