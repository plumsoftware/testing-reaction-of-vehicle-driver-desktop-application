package data.model.regular.user

import java.util.Calendar

data class Interval(
    val start: Long = 0L,
    val finish: Long = 0L
) {

    companion object {
        fun fromString(string: String): Interval {
            val split: List<String> = string.split(" - ")

            val cal1 = Calendar.getInstance()
            cal1.set(Calendar.SECOND, split[0].toInt())

            val cal2 = Calendar.getInstance()
            cal2.set(Calendar.SECOND, split[1].toInt())

            return Interval(start = cal1.timeInMillis, finish = cal2.timeInMillis)
        }
    }

    override fun toString(): String {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = start
        val startQ = cal.get(Calendar.SECOND)

        cal.timeInMillis = finish
        val finishQ = cal.get(Calendar.SECOND)

        return "$startQ - $finishQ"
    }
}
