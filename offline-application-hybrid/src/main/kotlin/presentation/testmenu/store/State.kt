package presentation.testmenu.store

import data.model.regular.user.User

data class State(
    val user: User = User.empty()
)
