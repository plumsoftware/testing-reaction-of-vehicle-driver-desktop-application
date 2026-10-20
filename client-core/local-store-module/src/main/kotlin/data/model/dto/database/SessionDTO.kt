package data.model.dto.database

import data.model.dto.test.PreTestState
import data.model.dto.test.TestEnvironment
import data.model.regular.user.DrivingLicenseCategory
import data.model.regular.user.Handedness
import data.model.regular.user.Interval

/**
 * Сессия в БД. Поля после [signalInterval] появились в версии методики 2:
 * у сессий, записанных раньше, они равны null / значениям по умолчанию.
 *
 * @param validCount учтено правильных реакций (n).
 * @param environment null — сведения о рабочем месте не записывались.
 */
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
    val signalInterval: Interval,

    val validCount: Int? = null,
    val wrongResponses: Int? = null,
    val omissions: Int? = null,
    val prematureResponses: Int? = null,
    val handedness: Handedness = Handedness.UNKNOWN,
    val userSessionNumber: Int? = null,
    val preTestState: PreTestState = PreTestState(),
    val environment: TestEnvironment? = null,
)
