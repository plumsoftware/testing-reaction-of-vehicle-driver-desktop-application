package presentation.tests.traffic_light_test.store

sealed class Effect {
    data object BackClicked : Effect()
}