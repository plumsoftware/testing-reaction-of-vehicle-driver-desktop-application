package data.constant

object GeneralConstants {
    val USER_NAME: String = System.getProperty("user.name")
    const val FOLDER_NAME = "Reaction test"

    object Paths {
        val PATH_TO_SETTINGS_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PATH_TO_SETTINGS_FILE = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}\\settings.json"

        val PATH_TO_LOCAL_SQL_FOLDER = "C:\\Users\\${USER_NAME}\\AppData\\Local\\${FOLDER_NAME}"
        val PART_PATH_TO_LOCAL_SQL_FOLDER = "C:\\Users\\${USER_NAME}"

        fun PATH_TO_ROAMING_DATABASE_DIRECTORY(dir: String) : String{
            return "${dir}Default\\AppData\\Roaming\\${FOLDER_NAME}"
        }
    }
}