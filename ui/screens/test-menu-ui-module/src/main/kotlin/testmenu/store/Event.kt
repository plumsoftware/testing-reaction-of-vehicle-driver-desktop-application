package testmenu.store

import data.model.dto.test.TestDTO

sealed class Event {
    data class TestClicked(val route: String, val testDTO: TestDTO) : Event()

    data object BackCLicked : Event()
}