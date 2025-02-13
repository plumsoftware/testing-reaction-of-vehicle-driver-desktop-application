package tests.traffic_light_test_choice_reaction.store

sealed class Event {
    data object BackCLicked : Event()

    data class OnTrafficLightLampButtonClicked(val clickedLampIndex: Int) : Event()

    data object StartTimer : Event()
}