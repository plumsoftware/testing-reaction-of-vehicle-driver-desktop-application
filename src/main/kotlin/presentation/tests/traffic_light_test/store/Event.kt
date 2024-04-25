package presentation.tests.traffic_light_test.store

sealed class Event {
    data object BackCLicked : Event()
}