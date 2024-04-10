package presentation.testmenu.store

sealed class Output {
    data object Test1Clicked : Output()
    data object BackButtonClicked : Output()
}