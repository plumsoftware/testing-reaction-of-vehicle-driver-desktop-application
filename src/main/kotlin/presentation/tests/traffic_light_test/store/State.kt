package presentation.tests.traffic_light_test.store

data class State(
    val startTimerTime: Int = 5,
    val currentLampIndex: Int = -1,
    val start: Long = 0L,
    val end: Long = 0L
)