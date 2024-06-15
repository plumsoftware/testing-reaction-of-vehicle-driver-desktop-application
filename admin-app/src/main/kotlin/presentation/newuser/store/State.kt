package presentation.newuser.store

import domain.model.either.AppEither
import domain.model.regular.user.Gender

data class State(
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val gender: Gender = Gender.EMPTY,
    val login: String = "",
    val password: String = "",

    val isNameError: Boolean = false,
    val isSurnameError: Boolean = false,
    val isGenderError: Boolean = false,
    val isLoginError: Boolean = false,
    val isPasswordError: Boolean = false,

    val appEither: AppEither = AppEither.Handle
)
