package tests.traffic_light_test_difference_reaction.store

sealed class Effect {
    data object BackClicked : Effect()
}