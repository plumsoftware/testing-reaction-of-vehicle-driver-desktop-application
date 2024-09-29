package tests.traffic_light_test.store

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data object StartTimer : Event()
}