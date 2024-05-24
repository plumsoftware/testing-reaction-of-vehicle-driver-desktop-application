package presentation.tests.traffic_light_test.store

import domain.model.dto.TestDTO
import domain.model.regular.TestMode

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data class InitStartData(val testDTO: TestDTO) : Event()
    data class InitTestMode(val testMode: TestMode) : Event()
}