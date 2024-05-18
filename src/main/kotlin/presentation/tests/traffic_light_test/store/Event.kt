package presentation.tests.traffic_light_test.store

import domain.model.dto.TestDTO

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data class InitStartData(val testDTO: TestDTO) : Event()
}