package presentation.testmenu.store

import data.model.regular.tests.TestMode

sealed class Effect {
    data class TestClicked(val route: String, val testMode: TestMode) : Effect()
    data object BackClicked : Effect()
}