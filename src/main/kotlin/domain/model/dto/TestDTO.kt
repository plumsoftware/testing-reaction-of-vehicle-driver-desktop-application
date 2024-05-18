package domain.model.dto

import domain.model.regular.Interval
import domain.model.regular.User

data class TestDTO(
    val user: User = User.empty(),
    val count: Int = 0,
    val interval: Interval = Interval()
)
