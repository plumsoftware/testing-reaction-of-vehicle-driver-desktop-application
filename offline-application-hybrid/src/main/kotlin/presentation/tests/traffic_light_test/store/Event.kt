package presentation.tests.traffic_light_test.store

import data.model.dto.test.TestDTO
import data.model.regular.tests.TestMode

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data class InitStartData(val testDTO: TestDTO) : Event()
    data class InitTestMode(val testMode: TestMode) : Event()
}