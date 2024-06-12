package domain.usecase.sqldelight

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval
import domain.repository.SQLDeLightRepository

class GetSessionsWithUserIdUseCase(private val sqlDeLightRepository: SQLDeLightRepository) {
    suspend fun execute(userId: Long): List<SessionDTO> {
        val sessionsWithUserId = sqlDeLightRepository.getSessionsWithUserId(id = userId).map {
            SessionDTO(
                sessionId = it.session_id,
                userId = it.user_id,
                testId = it.test_id,
                testYear = it.test_year.toInt(),
                testMonth = it.test_month.toInt(),
                testDay = it.test_day.toInt(),
                testHourOfDay24h = it.test_hour_of_day_24h.toInt(),
                testMinuteOfHour = it.test_minute_of_hour.toInt(),
                averageValue = kotlin.math.ceil(it.average_value * 10) / 10,
                standardDeviation = it.standard_deviation,
                count = it.count.toInt(),
                errors = it.errors.toInt(),
                experience = it.experience.toInt(),
                drivingLicenseCategory = DrivingLicenseCategory.fromString(it.driving_license_category),
                signalInterval = Interval.fromString(it.signal_interval)
            )
        }
        return sessionsWithUserId
    }
}