package presentation.newuser.store

sealed class Effect {
    data object BackClicked : Effect()
}