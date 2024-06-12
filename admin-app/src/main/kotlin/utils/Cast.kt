package utils

import domain.model.dto.database.SessionDTO
import domain.model.regular.userstatistic.LineChartItem
import java.text.SimpleDateFormat
import java.util.*

//region::Charts
//region::Line charts
fun List<SessionDTO>.toLineChartData(): List<LineChartItem> {
    return this.map {

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, it.testYear)
        cal.set(Calendar.MONTH, it.testMonth)
        cal.set(Calendar.DAY_OF_MONTH, it.testDay)
        cal.set(Calendar.HOUR_OF_DAY, it.testHourOfDay24h)
        cal.set(Calendar.MINUTE, it.testMinuteOfHour)

        LineChartItem(
            valueX = cal.timeInMillis,
            valueY = it.averageValue
        )
    }
}

fun List<SessionDTO>.toLineChartData1(): List<LineChartItem> {
    return this.map {

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, it.testYear)
        cal.set(Calendar.MONTH, it.testMonth)
        cal.set(Calendar.DAY_OF_MONTH, it.testDay)
        cal.set(Calendar.HOUR_OF_DAY, it.testHourOfDay24h)
        cal.set(Calendar.MINUTE, it.testMinuteOfHour)

        LineChartItem(
            valueX = cal.timeInMillis,
            valueY = it.standardDeviation
        )
    }
}

fun List<SessionDTO>.toLineChartData2(): List<LineChartItem> {
    return this.map {

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, it.testYear)
        cal.set(Calendar.MONTH, it.testMonth)
        cal.set(Calendar.DAY_OF_MONTH, it.testDay)
        cal.set(Calendar.HOUR_OF_DAY, it.testHourOfDay24h)
        cal.set(Calendar.MINUTE, it.testMinuteOfHour)

        LineChartItem(
            valueX = cal.timeInMillis,
            valueY = it.errors.toDouble()
        )
    }
}

fun List<SessionDTO>.toLineChartData3(): List<LineChartItem> {
    return this.map {

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, it.testYear)
        cal.set(Calendar.MONTH, it.testMonth)
        cal.set(Calendar.DAY_OF_MONTH, it.testDay)
        cal.set(Calendar.HOUR_OF_DAY, it.testHourOfDay24h)
        cal.set(Calendar.MINUTE, it.testMinuteOfHour)

        LineChartItem(
            valueX = cal.timeInMillis,
            valueY = it.count.toDouble()
        )
    }
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