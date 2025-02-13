package about_app.store

sealed class Effect {
    data object BackClick : Effect()
}