package data.repository

import app.cash.sqldelight.db.SqlDriver
import domain.model.dto.database.SessionDTO
import domain.repository.SessionRepository
import ru.plumsoftware.sessions.Database
import ru.plumsoftware.sessions.Sessions

class SessionRepositoryImpl(
    private val driver: SqlDriver
) : SessionRepository {
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
                    errors = errors.toInt()
                )
            }
        }

        return list
    }

    override suspend fun insertOrAbortNewSession(sessionDTO: SessionDTO) {
        val database = Database(driver = driver)
        with(sessionDTO) {
            database.sqldelight_schemeQueries.insertOrAbortNewSession(
                sessionId,
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
                errors.toLong()
            )
        }
    }
}