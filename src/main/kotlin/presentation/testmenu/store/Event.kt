package presentation.testmenu.store

import domain.model.regular.tests.TestMode

sealed class Event {
    data class TestClicked(val route: String, val testMode: TestMode) : Event()
    data object BackCLicked : Event()
}