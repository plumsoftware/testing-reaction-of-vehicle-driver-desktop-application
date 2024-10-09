package tests.traffic_light_test_choice_reaction.store

sealed class Effect {
    data object BackClicked : Effect()
}