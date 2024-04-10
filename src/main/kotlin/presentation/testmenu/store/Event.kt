package presentation.testmenu.store

sealed class Event {
    data object Test1CLicked : Event()
    data object BackCLicked : Event()
}