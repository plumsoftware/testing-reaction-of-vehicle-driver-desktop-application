package presentation.aboutuser.store

sealed class Effect {
    data object BackClicked : Effect()
}