package domain.model.dto

import domain.model.regular.user.Interval
import domain.model.regular.tests.TestMode
import domain.model.regular.user.User

data class TestDTO(
    val user: User = User.empty(),
    val count: Int = 0,
    val interval: Interval = Interval(),

    val testMode: TestMode? = null,
    val intervals: List<Long> = listOf(),
    val errorsCount: Int? = null,
    val sessionId: Long = 0L,
    val averageValue: Double = 0.0,
    val stdDeviationValue: Double = 0.0
)
