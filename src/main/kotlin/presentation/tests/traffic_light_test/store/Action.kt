package presentation.tests.traffic_light_test.store

sealed class Action {
    data object StartTimer : Action()
    data object GenerateRandomSignal : Action()
}