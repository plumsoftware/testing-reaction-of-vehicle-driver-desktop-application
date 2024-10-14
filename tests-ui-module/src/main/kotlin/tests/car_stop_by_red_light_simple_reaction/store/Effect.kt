package tests.car_stop_by_red_light_simple_reaction.store

sealed class Effect {
    data object BackClicked : Effect()
}