package presentation.testmenu.store

sealed class Event {
    data class TestClicked(val route: String) : Event()
    data object BackCLicked : Event()
}