package presentation.authorization.auth.store

import domain.model.regular.User

data class State (
    val user: User
)
