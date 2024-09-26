package data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.constant.DatabaseConstants
import data.constant.GeneralConstants
import domain.repository.SessionRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.GetLastSessionId
import ru.plumsoftware.sessions.Sessions
import utlis.createFolderIfNotExists
import data.model.dto.database.SessionDTO
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval

class SessionRepositoryImpl : SessionRepository {

    init {
        createFolderIfNotExists(directoryPath = GeneralConstants.Paths.PATH_TO_LOCAL_SQL_FOLDER)
        val driver: SqlDriver = JdbcSqliteDriver(DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME)
        val database = Database(driver = driver)
        database.sqldelight_sessions_schemeQueries.create()
    }

    override suspend fun getAllSessionDtoFromDatabase(): List<SessionDTO> {
        val driver: SqlDriver = JdbcSqliteDriver(DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME)
        val database = Database(driver = driver)
        val executeAsList: List<Sessions> = database.sqldelight_sessions_schemeQueries.selectAllSessions().executeAsList()

        val list: List<SessionDTO> = executeAsList.map {
            with(it) {
                SessionDTO(
                    sessionId = session_id,
                    userId = user_id,
                    testId = test_id,
                    testYear = test_year.toInt(),
                    testMonth = test_month.toInt(),
                    testDay = test_day.toInt(),
                    testHourOfDay24h = test_hour_of_day_24h.toInt(),
                    testMinuteOfHour = test_minute_of_hour.toInt(),
                    averageValue = average_value,
                    standardDeviation = standard_deviation,
                    count = count.toInt(),
                    errors = errors.toInt(),
                    experience = experience.toInt(),
                    userAge = user_age.toInt(),
                    drivingLicenseCategory = DrivingLicenseCategory.valueOf(driving_license_category),
                    signalInterval = Interval.fromString(signal_interval)
                )
            }
        }

        return list
    }

    override suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO) {
        val driver: SqlDriver = JdbcSqliteDriver(DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME)
        val database = Database(driver = driver)
        with(sessionDTO) {
            database.sqldelight_sessions_schemeQueries.transaction {
                database.sqldelight_sessions_schemeQueries.insertOrAbortNewSession(
                    userId,
                    testId,
                    testYear.toLong(),
                    testMonth.toLong(),
                    testDay.toLong(),
                    testHourOfDay24h.toLong(),
                    testMinuteOfHour.toLong(),
                    averageValue,
                    standardDeviation,
                    count.toLong(),
                    errors.toLong(),
                    experience.toLong(),
                    userAge.toLong(),
                    drivingLicenseCategory.toString(),
                    signalInterval.toString()
                )
            }
        }
    }

    override suspend fun getLastSessionId(): Long {
        val driver: SqlDriver = JdbcSqliteDriver(DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME)
        val database = Database(driver = driver)
        val executeAsList: List<GetLastSessionId> = database.sqldelight_sessions_schemeQueries.getLastSessionId().executeAsList()
        return executeAsList[0].MAX ?: 0L
    }
}