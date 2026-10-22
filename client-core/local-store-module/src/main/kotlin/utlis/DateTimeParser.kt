package utlis

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun parseToExcelDateTime(timeInMillis: Long): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timeInMillis))
}
/** Дата и время с секундами для БД (`yyyy-MM-dd HH:mm:ss`, местное время): этот формат понимают функции дат SQLite. */
fun formatDbDateTime(timeInMillis: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(Date(timeInMillis))

/** Дата и время с секундами для Excel. */
fun formatExcelDateTimeSeconds(timeInMillis: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT).format(Date(timeInMillis))
