package presentation.users.store

import data.model.regular.user.User

sealed class Effect {
    data object BackCLicked : Effect()
    data class UserCLicked(val selectedUser: User) : Effect()
}