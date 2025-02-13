package tests.traffic_light_test_choice_reaction.store

import data.model.dto.test.TestDTO

data class State(
    val startTimerTime: Int = 10,
    val currentLampIndex: Int = -1,
    val start: Long = 0L,
    val end: Long = 0L,
    val userClicked: Int = 0,

    val intervals: MutableList<Long> = mutableListOf(),
    val errors: Int = 0,

    val testDTO: TestDTO = TestDTO(),

    val dataFormats: Map<String, Boolean>,
    val localFolderToTable: String,
)