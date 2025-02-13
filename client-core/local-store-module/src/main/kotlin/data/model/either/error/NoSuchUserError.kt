package data.model.either.error

data class NoSuchUserError(override val message: String = "Нет пользователя с таким логином и паролем") :
    Exception()
