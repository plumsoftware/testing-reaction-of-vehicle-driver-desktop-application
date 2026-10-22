package newuser.store

import data.model.either.AppEither
import data.model.regular.user.Gender
import data.model.regular.user.Handedness

data class State(
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val gender: Gender = Gender.EMPTY,
    val handedness: Handedness = Handedness.UNKNOWN,
    val login: String = "",
    val password: String = "",

    val isNameError: Boolean = false,
    val isSurnameError: Boolean = false,
    val isGenderError: Boolean = false,
    val isHandednessError: Boolean = false,
    val isLoginError: Boolean = false,
    val isPasswordError: Boolean = false,

    val appEither: AppEither = AppEither.Handle
)
