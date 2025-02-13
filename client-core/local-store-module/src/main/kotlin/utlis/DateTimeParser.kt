package utlis

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun parseToExcelDateTime(timeInMillis: Long): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timeInMillis))
}