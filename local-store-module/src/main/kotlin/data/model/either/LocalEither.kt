package data.model.either

sealed class LocalEither<out E, out D> {

    data class Exception<out E, out D>(val a: E) : LocalEither<E, D>()

    data class Data<out E, out D>(val b: D) : LocalEither<E, D>()

}

fun <E> E.Exception() = LocalEither.Exception<E, Nothing>(this)

fun <T> T.Data() = LocalEither.Data<Nothing, T>(this)