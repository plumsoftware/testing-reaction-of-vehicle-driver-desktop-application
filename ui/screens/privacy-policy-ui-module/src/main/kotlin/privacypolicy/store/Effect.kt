package privacypolicy.store

sealed class Effect {
    data object BackClick : Effect()
}