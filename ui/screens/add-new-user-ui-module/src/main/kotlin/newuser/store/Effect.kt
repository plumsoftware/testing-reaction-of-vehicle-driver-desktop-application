package newuser.store

sealed class Effect {
    data object BackClicked : Effect()
}