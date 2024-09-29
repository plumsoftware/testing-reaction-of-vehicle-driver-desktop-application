package data.model.either

sealed class AppEither{

    data object Handle : AppEither()

    data class Exception<out E>(val e: E) : AppEither()

    data class Success<out M>(val e: M) : AppEither()

}