package data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.Constants
import domain.model.dto.database.SessionDTO
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval
import domain.repository.SessionRepository
import ru.plumsoftware.sessions.Database
import ru.plumsoftware.sessions.GetLastSessionId
import ru.plumsoftware.sessions.Sessions
import utils.createFolderIfNotExists

class SessionRepositoryImpl : SessionRepository {

    private val driver: SqlDriver = JdbcSqliteDriver(Constants.Database.LOCAL_JDBC_DRIVER_NAME)

    init {
        createFolderIfNotExists(folderPath = Constants.General.PATH_TO_LOCAL_SQL_FOLDER)
        val database = Database(driver = driver)
        database.sqldelight_schemeQueries.create()
    }

    override suspend fun getAllSessionDtoFromDatabase(): List<SessionDTO> {
        val database = Database(driver = driver)
        val executeAsList: List<Sessions> = database.sqldelight_schemeQueries.selectAllSessions().executeAsList()

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
                    drivingLicenseCategory = DrivingLicenseCategory.valueOf(driving_license_category),
                    signalInterval = Interval.fromString(signal_interval)
                )
            }
        }

        return list
    }

    override suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO) {
        val database = Database(driver = driver)
        with(sessionDTO) {
            database.sqldelight_schemeQueries.transaction {
                database.sqldelight_schemeQueries.insertOrAbortNewSession(
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
                    drivingLicenseCategory.toString(),
                    signalInterval.toString()
                )
            }
        }
    }

    override suspend fun getLastSessionId(): Long {
        val database = Database(driver = driver)
        val executeAsList: List<GetLastSessionId> = database.sqldelight_schemeQueries.getLastSessionId().executeAsList()
        return executeAsList[0].MAX ?: 0L
    }
}