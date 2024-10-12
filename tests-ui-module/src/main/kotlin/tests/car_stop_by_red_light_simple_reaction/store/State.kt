package tests.car_stop_by_red_light_simple_reaction.store

import data.model.dto.test.TestDTO

data class State(
    val start: Long = 0L,
    val end: Long = 0L,
    val userClicked: Int = 0,
    val errors: Int = 0,

    val intervals: MutableList<Long> = mutableListOf(),
    val testDTO: TestDTO = TestDTO(),

    val startCountDownTimer: Int = 10,
    val intervalSignal: Long = 0L
)