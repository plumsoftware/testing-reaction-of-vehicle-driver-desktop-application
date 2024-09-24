package domain.usecase.sql_database.local.user

import domain.model.dto.database.SessionDTO
import domain.model.regular.user.DrivingLicenseCategory
import domain.model.regular.user.Interval
import domain.repository.UserRepository

class GetSessionsWithUserIdUseCase(private val userRepository: UserRepository) {
    suspend fun execute(userId: Long): List<SessionDTO> {
        val sessionsWithUserId = userRepository.getSessionsWithUserId(id = userId).map {
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
                standardDeviation = kotlin.math.ceil(it.standard_deviation * 10) / 10,
                count = it.count.toInt(),
                errors = it.errors.toInt(),
                experience = it.experience.toInt(),
                drivingLicenseCategory = DrivingLicenseCategory.fromString(it.driving_license_category),
                signalInterval = Interval.fromString(it.signal_interval),
                userAge = it.user_age.toInt()
            )
        }.reversed()
        return sessionsWithUserId
    }
}