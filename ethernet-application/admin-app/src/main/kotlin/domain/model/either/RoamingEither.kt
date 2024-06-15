package domain.model.either

sealed class RoamingEither<out E, out D> {

    data class Exception<out E, out D>(val a: E) : RoamingEither<E, D>()

    data class Data<out E, out D>(val b: D) : RoamingEither<E, D>()

}

fun <E> E.Exception() = RoamingEither.Exception<E, Nothing>(this)

fun <T> T.Data() = RoamingEither.Data<Nothing, T>(this)