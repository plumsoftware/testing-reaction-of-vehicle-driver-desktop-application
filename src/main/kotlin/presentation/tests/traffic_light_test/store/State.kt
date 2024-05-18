package presentation.tests.traffic_light_test.store

import domain.model.regular.Interval
import domain.model.regular.User

data class State(
    val startTimerTime: Int = 5,
    val currentLampIndex: Int = -1,
    val start: Long = 0L,
    val end: Long = 0L,
    val user: User = User.empty(),
    val count: Int = 0,
    val userClicked: Int = 0,
    val signalInterval: Interval = Interval(),

    val intervals: MutableList<Long> = mutableListOf(),
    val errors: Int = 0
)