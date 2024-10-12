package tests.car_stop_by_red_light_simple_reaction.store

sealed class Event {
    data object StartInitTimer : Event()
    data object ClickOnStopPedal : Event()
}