package data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.Constants
import domain.model.either.RoamingEither
import domain.repository.UserRepository
import ru.plumsoftware.sessions.Database
import ru.plumsoftware.users.Users

class UserRepositoryImpl(netDriver: String) : UserRepository {
    private var driver: SqlDriver = JdbcSqliteDriver(Constants.Database.collapseNetDriver(netDriver = netDriver))

    override suspend fun getUserByLoginAndPassword(
        login: String,
        password: String
    ): RoamingEither<Exception, List<Users>> {
        val database = Database(driver = driver)
        try {
            val executeAsList: List<Users> = database.sqldelight_users_schemeQueries.getUserByLoginAndPassword(
                user_login = login,
                user_password = password
            ).executeAsList()

            return RoamingEither.Data(executeAsList)

        } catch (e: Exception) {
            return RoamingEither.Exception(e)
        }
    }
}