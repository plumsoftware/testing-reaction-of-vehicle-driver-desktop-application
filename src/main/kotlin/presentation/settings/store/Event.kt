package presentation.settings.store

sealed class Event {
    data object BackClicked : Event()

    data class OnCheckboxChanged(val isChecked: Boolean) : Event()
}