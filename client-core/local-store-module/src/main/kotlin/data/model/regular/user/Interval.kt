package data.model.regular.user

/**
 * Диапазон случайного интервала до сигнала, мс.
 * Строковое представление — «секунды - секунды» (например, «2 - 5»): так оно хранится в БД.
 */
data class Interval(
    val start: Long = 0L,
    val finish: Long = 0L
) {

    companion object {
        fun fromString(string: String): Interval {
            val split: List<String> = string.split(" - ")
            return Interval(start = split[0].trim().toLong() * 1000, finish = split[1].trim().toLong() * 1000)
        }
    }

    override fun toString(): String = "${start / 1000} - ${finish / 1000}"
}
