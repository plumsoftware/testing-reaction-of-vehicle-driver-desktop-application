package presentation.authorization.auth.store

import domain.model.regular.user.User

data class State (
    val user: User
)
