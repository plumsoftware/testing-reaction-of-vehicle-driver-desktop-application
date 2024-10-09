package tests.traffic_light_test_difference_reaction.store

sealed class Event {
    data object BackCLicked : Event()

    data object OnTrafficLightLampButtonClicked : Event()

    data object StartTimer : Event()
}