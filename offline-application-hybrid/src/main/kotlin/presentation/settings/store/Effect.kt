package presentation.settings.store

sealed class Effect {
    data object BackClicked : Effect()
}