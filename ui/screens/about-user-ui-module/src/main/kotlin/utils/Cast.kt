package utils

import data.model.dto.database.SessionDTO
import model.userstatistic.LineChartItem
import java.text.SimpleDateFormat
import java.util.*

//region::Charts
//region::Line charts
/** Точки графика по сессиям: x — дата и время сессии, y — [value]. */
fun List<SessionDTO>.toLineChartData(value: (SessionDTO) -> Double): List<LineChartItem> {
    return this.map {
        LineChartItem(
            valueX = it.timeInMillis(),
            valueY = value(it)
        )
    }
}

fun SessionDTO.timeInMillis(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, testYear)
    // testMonth хранится как Calendar.MONTH (с нуля)
    cal.set(Calendar.MONTH, testMonth)
    cal.set(Calendar.DAY_OF_MONTH, testDay)
    cal.set(Calendar.HOUR_OF_DAY, testHourOfDay24h)
    cal.set(Calendar.MINUTE, testMinuteOfHour)
    return cal.timeInMillis
}

fun List<LineChartItem>.x(): List<Long> {
    return this.map {
        it.valueX
    }
}

fun List<Long>.toSemiDate(): List<String> {
    return this.map {
        val str = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(it))
        return@map str
    }
}

fun List<LineChartItem>.y(): List<Double> {
    return this.map {
        it.valueY
    }
}
//endregion
//endregion
