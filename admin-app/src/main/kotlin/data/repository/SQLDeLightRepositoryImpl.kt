package data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.Constants
import domain.model.dto.database.SessionDTO
import domain.model.regular.user.User
import domain.repository.SQLDeLightRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.Sessions
import ru.plumsoftware.users.Users
import utils.createFolderIfNotExists

class SQLDeLightRepositoryImpl : SQLDeLightRepository {

    private val path = Constants.Database.collapseNetDriver(netDriver = "D:\\")

    private val driver: SqlDriver = JdbcSqliteDriver(path)

    init {
        createFolderIfNotExists(folderPath = path)
        val database = Database(driver = driver)
        database.sqldelight_users_schemeQueries.create()
    }

    override suspend fun getAllUsers(): List<Users> {
        val database = Database(driver = driver)
        return database.sqldelight_users_schemeQueries.getAllUsers().executeAsList()
    }

    override suspend fun getAllSessions(): List<SessionDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getSessionsWithUserId(id: Long): List<Sessions> {
        val database = Database(driver = driver)
        return database.sqldelight_schemeQueries.getSessionsWithUserId(user_id = id).executeAsList()
    }

    override suspend fun insertNewUser(user: User, login: String, password: String) {
        val database = Database(driver = driver)
        with(user) {
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

    override suspend fun updateUser(user: User, login: String, password: String) {
        val database = Database(driver = driver)
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
        val database = Database(driver = driver)
        database.sqldelight_users_schemeQueries.deleteUser(user_id = id)
    }
}