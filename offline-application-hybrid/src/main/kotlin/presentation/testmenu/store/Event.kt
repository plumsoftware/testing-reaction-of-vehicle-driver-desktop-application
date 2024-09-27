package presentation.testmenu.store

import data.model.regular.tests.TestMode
import data.model.regular.user.User

sealed class Event {
    data class TestClicked(val route: String, val testMode: TestMode) : Event()

    data object BackCLicked : Event()
}