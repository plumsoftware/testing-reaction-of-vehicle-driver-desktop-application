package data.constant

object DatabaseConstants {
    private const val SESSION_DATABASE_NAME = "sessions_database.db"
    private const val USER_DATABASE_NAME = "user_database.db"

    val LOCAL_SESSION_JDBC_DRIVER_NAME = "jdbc:sqlite:C:\\Users\\${GeneralConstants.USER_NAME}\\AppData\\Local\\${GeneralConstants.FOLDER_NAME}\\${SESSION_DATABASE_NAME}"
    val LOCAL_USER_JDBC_DRIVER_NAME = "jdbc:sqlite:C:\\Users\\${GeneralConstants.USER_NAME}\\AppData\\Local\\${GeneralConstants.FOLDER_NAME}\\${USER_DATABASE_NAME}"

    fun collapseNetDriver(netDriver: String): String {
        val driver = "${netDriver.split(":")[0]}:"
        return "jdbc:sqlite:${driver}\\Default\\AppData\\Roaming\\${GeneralConstants.FOLDER_NAME}\\${USER_DATABASE_NAME}"
    }

    fun collapseNetDriverToSessions(netDriver: String): String {
        val driver = "${netDriver.split(":")[0]}:"
        return "jdbc:sqlite:${driver}\\Default\\AppData\\Roaming\\${GeneralConstants.FOLDER_NAME}\\${SESSION_DATABASE_NAME}"
    }
}