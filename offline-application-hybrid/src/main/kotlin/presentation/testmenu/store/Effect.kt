package presentation.testmenu.store

import data.model.dto.test.TestDTO

sealed class Effect {
    data class TestClicked(val route: String, val testDTO: TestDTO) : Effect()
    data object BackClicked : Effect()
}