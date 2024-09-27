package presentation.testmenu.store

import model.tests.ReactionTest

data class State(
    val id: Long = -1L,
    val reactionTests: List<ReactionTest>,
)
