package presentation.testmenu.store

import domain.model.regular.tests.TestMode

sealed class Output {
    data class TestClicked(val route: String, val testMode: TestMode) : Output()
    data object BackButtonClicked : Output()
}