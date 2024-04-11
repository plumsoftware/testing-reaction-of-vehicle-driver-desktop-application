package presentation.testmenu.store

sealed class Output {
    data class TestClicked(val route: String) : Output()
    data object BackButtonClicked : Output()
}