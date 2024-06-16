package presentation.testmenu.store

import domain.model.regular.user.User

data class State(
    val user: User = User.empty()
)
