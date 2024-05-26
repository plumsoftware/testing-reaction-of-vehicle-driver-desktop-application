package domain.model.dto

data class SessionDTO(
    val sessionId: Int,
    val userId: Int,
    val testId: Int,
    val testYear: Int,
    val testMonth: Int,
    val testDay: Int,
    val testHourOfDay24h: Int,
    val testMinuteOfHour: Int,
    val averageValue: Double,
    val standardDeviation: Double,
    val count: Int,
    val error: Int
)
