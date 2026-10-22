package tests.horn_brake_reaction.store

sealed class Effect {
    data object BackClicked : Effect()
}
