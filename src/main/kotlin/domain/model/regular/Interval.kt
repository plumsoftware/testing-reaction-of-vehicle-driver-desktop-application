package domain.model.regular

import java.util.Calendar

data class Interval(
    val start: Long = 0L,
    val finish: Long = 0L
){
    override fun toString(): String {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = start
        val startQ = cal.get(Calendar.SECOND)

        cal.timeInMillis = finish
        val finishQ = cal.get(Calendar.SECOND)

        return "$startQ - $finishQ"
    }
}
