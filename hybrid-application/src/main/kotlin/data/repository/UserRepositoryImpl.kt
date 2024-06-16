package data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.Constants
import domain.model.either.LocalEither
import domain.model.regular.user.User
import domain.repository.UserRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.Sessions
import ru.plumsoftware.users.Users
import utils.createFolderIfNotExists

class UserRepositoryImpl : UserRepository {

    init {
        createFolderIfNotExists(folderPath = "C:\\Users\\${Constants.USER_NAME}\\AppData")
        createFolderIfNotExists(folderPath = "C:\\Users\\${Constants.USER_NAME}\\AppData\\Local")
        createFolderIfNotExists(folderPath = "C:\\Users\\${Constants.USER_NAME}\\AppData\\Local\\${Constants.FOLDER_NAME}")

        val userDatabase = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        userDatabase.sqldelight_users_schemeQueries.create()

        val sessionsDatabase =
            Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_SESSION_JDBC_DRIVER_NAME))
        sessionsDatabase.sqldelight_schemeQueries.create()
    }

    override suspend fun getAllUsers(): List<Users> {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        return database.sqldelight_users_schemeQueries.getAllUsers().executeAsList().reversed()
    }

    override suspend fun getSessionsWithUserId(id: Long): List<Sessions> {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_SESSION_JDBC_DRIVER_NAME))
        return database.sqldelight_schemeQueries.getSessionsWithUserId(user_id = id).executeAsList()
    }

    @Throws(Exception::class)
    override suspend fun insertNewUser(user: User) {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        with(user) {
            database.sqldelight_users_schemeQueries.transaction {
                database.sqldelight_users_schemeQueries.insertNewUser(
                    user_login = login,
                    user_password = password,
                    user_name = name,
                    user_surname = surname,
                    user_patronymic = patronymic,
                    gender = gender.toString(),
                )
            }
        }
    }

    override suspend fun getAllPasswords(): List<String> {

        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        val selectAllPasswords = database.sqldelight_users_schemeQueries.selectAllPasswords().executeAsList()

        return selectAllPasswords
    }

    override suspend fun updateUser(user: User) {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        with(user) {
            database.sqldelight_users_schemeQueries.updateUser(
                user_login = login,
                user_password = password,
                user_name = name,
                user_surname = surname,
                user_patronymic = patronymic,
                gender = gender.toString(),
                user_id = id
            )
        }
    }

    override suspend fun deleteUser(id: Long) {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        database.sqldelight_users_schemeQueries.deleteUser(user_id = id)
    }

    override suspend fun getUserByLoginAndPassword(
        login: String,
        password: String
    ): LocalEither<Exception, List<Users>> {
        val database = Database(driver = JdbcSqliteDriver(url = Constants.Database.LOCAL_USER_JDBC_DRIVER_NAME))
        try {
            val executeAsList: List<Users> = database.sqldelight_users_schemeQueries.getUserByLoginAndPassword(
                user_login = login,
                user_password = password
            ).executeAsList()

            return LocalEither.Data(executeAsList)

        } catch (e: Exception) {
            return LocalEither.Exception(e)
        }
    }
}