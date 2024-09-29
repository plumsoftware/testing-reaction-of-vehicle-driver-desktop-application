package data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import data.constant.DatabaseConstants
import data.constant.GeneralConstants
import domain.repository.SessionRepository
import ru.plumsoftware.Database
import ru.plumsoftware.sessions.GetLastSessionId
import ru.plumsoftware.sessions.Sessions
import utlis.createFolderIfNotExists
import data.model.dto.database.SessionDTO
import data.model.regular.Mode
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval
import utlis.getSessionsDatabaseDriver
import utlis.getUsersDatabaseDriver

class SessionRepositoryImpl_(
    mode: Mode = Mode.SINGLE,
    directoryPath: String = GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER,
    usersDirectory: String = DatabaseConstants.LOCAL_USER_JDBC_DRIVER_NAME,
    sessionsDirectory: String = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
) : SessionRepository {

    private val usersDatabaseDriver: JdbcSqliteDriver
    private val sessionsDatabaseDriver: JdbcSqliteDriver
    private val dir = if (mode == Mode.SINGLE) GeneralConstants.Paths.PATH_TO_SETTINGS_FOLDER else GeneralConstants.Paths.PATH_TO_ROAMING_DATABASE_DIRECTORY(directoryPath)


    init {
        createFolderIfNotExists(directoryPath = dir)
        usersDatabaseDriver = if (mode == Mode.ETHERNET) getUsersDatabaseDriver(networkDrive = usersDirectory) else JdbcSqliteDriver(
            url = DatabaseConstants.LOCAL_USER_JDBC_DRIVER_NAME
        )

        sessionsDatabaseDriver = if (mode == Mode.ETHERNET) getSessionsDatabaseDriver(networkDrive = sessionsDirectory) else JdbcSqliteDriver(
            url = DatabaseConstants.LOCAL_SESSION_JDBC_DRIVER_NAME
        )

        val userDatabase =
            Database(driver = usersDatabaseDriver)
        userDatabase.sqldelight_users_schemeQueries.create()

        val sessionsDatabase =
            Database(driver = sessionsDatabaseDriver)
        sessionsDatabase.sqldelight_users_schemeQueries.create()
    }

    override suspend fun getAllSessionDtoFromDatabase(): List<SessionDTO> {
        val driver = usersDatabaseDriver
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
        val driver = sessionsDatabaseDriver
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
        val driver = sessionsDatabaseDriver
        val database = Database(driver = driver)
        val executeAsList: List<GetLastSessionId> = database.sqldelight_sessions_schemeQueries.getLastSessionId().executeAsList()
        return executeAsList[0].MAX ?: 0L
    }
}