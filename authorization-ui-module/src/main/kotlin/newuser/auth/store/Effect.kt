package newuser.auth.store

sealed class Effect {
    data object GoBack : Effect()
}