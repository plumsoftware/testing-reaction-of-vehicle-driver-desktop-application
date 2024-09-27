package authorization.auth.store

sealed class Effect {
    data object GoBack : Effect()
}