package data.model.dto.database

import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Interval

data class SessionDTO(
    val sessionId: Long,
    val userId: Long,
    val testId: Long,
    val testYear: Int,
    val testMonth: Int,
    val testDay: Int,
    val testHourOfDay24h: Int,
    val testMinuteOfHour: Int,
    val averageValue: Double,
    val standardDeviation: Double,
    val count: Int,
    val errors: Int,
    val experience: String,
    val userAge: String,
    val drivingLicenseCategory: DrivingLicenseCategory,
    val signalInterval: Interval
)
