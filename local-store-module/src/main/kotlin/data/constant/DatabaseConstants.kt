package data.constant

object DatabaseConstants {
    private const val LOCAL_SESSION_DATABASE_NAME = "sessions_database.db"
    private const val LOCAL_USER_DATABASE_NAME = "user_database.db"

    val LOCAL_SESSION_JDBC_DRIVER_NAME = "jdbc:sqlite:C:\\Users\\${GeneralConstants.USER_NAME}\\AppData\\Local\\${GeneralConstants.FOLDER_NAME}\\${LOCAL_SESSION_DATABASE_NAME}"
    val LOCAL_USER_JDBC_DRIVER_NAME = "jdbc:sqlite:C:\\Users\\${GeneralConstants.USER_NAME}\\AppData\\Local\\${GeneralConstants.FOLDER_NAME}\\${LOCAL_USER_DATABASE_NAME}"
}