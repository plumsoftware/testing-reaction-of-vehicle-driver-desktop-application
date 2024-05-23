package presentation.main

sealed class Event {
    data object StartTrafficLightAction : Event()
}