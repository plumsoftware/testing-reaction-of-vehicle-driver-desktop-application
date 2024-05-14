package presentation.tests.traffic_light_test.store

import domain.model.User

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data class InitStartData(val user: User, val count: Int) : Event()
}