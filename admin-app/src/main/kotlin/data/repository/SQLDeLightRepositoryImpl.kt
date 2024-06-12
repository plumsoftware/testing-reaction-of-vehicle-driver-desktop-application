package data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.Constants
import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.Sessions
import ru.plumsoftware.users.Users
import utils.createFolderIfNotExists

class SQLDeLightRepositoryImpl(private val networkDrive: String) : SQLDeLightRepository {

    private fun getDriver(networkDrive: String): SqlDriver {
        val path = Constants.Database.collapseNetDriver(netDriver = networkDrive)
        val driver: SqlDriver = JdbcSqliteDriver(path)
        return driver
    }

    private fun getSessionsDriver(networkDrive: String): SqlDriver {
        val path = Constants.Database.collapseNetDriverToSessions(netDriver = networkDrive)
        val driver: SqlDriver = JdbcSqliteDriver(path)
        return driver
    }

    init {
        if (networkDrive.isNotEmpty()) {
            val driverStr = "${networkDrive.split(":")[0]}:"
            createFolderIfNotExists(folderPath = "${driverStr}\\AppData")
            createFolderIfNotExists(folderPath = "${driverStr}\\AppData\\Roaming")
            createFolderIfNotExists(folderPath = "${driverStr}\\AppData\\Roaming\\${Constants.FOLDER_NAME}")

            val userDatabase = Database(driver = getDriver(networkDrive))
            userDatabase.sqldelight_users_schemeQueries.create()

            val sessionsDatabase = Database(driver = getSessionsDriver(networkDrive))
            sessionsDatabase.sqldelight_schemeQueries.create()
        }
    }

    override suspend fun getAllUsers(): List<Users> {
        return if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getDriver(networkDrive))
            database.sqldelight_users_schemeQueries.getAllUsers().executeAsList()
        } else {
            emptyList()
        }
    }

    override suspend fun getSessionsWithUserId(id: Long): List<Sessions> {
        return if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getSessionsDriver(networkDrive))
            database.sqldelight_schemeQueries.getSessionsWithUserId(user_id = id).executeAsList()
        } else {
            emptyList()
        }
    }

    @Throws(Exception::class)
    override suspend fun insertNewUser(user: User) {
        if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getDriver(networkDrive))
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
    }

    override suspend fun getAllPasswords(): List<String> {
        return if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getDriver(networkDrive))
            val selectAllPasswords = database.sqldelight_users_schemeQueries.selectAllPasswords().executeAsList()
            selectAllPasswords
        } else
            emptyList()
    }

    override suspend fun updateUser(user: User) {
        if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getDriver(networkDrive))
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
    }

    override suspend fun deleteUser(id: Long) {
        if (networkDrive.isNotEmpty()) {
            val database = Database(driver = getDriver(networkDrive))
            database.sqldelight_users_schemeQueries.deleteUser(user_id = id)
        }
    }
}