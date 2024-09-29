package presentation.settings.store

sealed class Effect {
    data object BackClick : Effect()
}