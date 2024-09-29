package other.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TestFinishedComponent(
    getAverage: () -> Double,
    getStdDeviation: () -> Double,
    count: Int,
    errors: Int,
    id: Long
) {
    Text(
        text = "Тестирование окончено",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Среднее время реакции ${"%.1f".format(getAverage())} секунд",
        style = MaterialTheme.typography.bodyMedium
    )
    Text(
        text = "Стандартное отклонение времени реакции ${"%.2f".format(getStdDeviation())} секунд",
        style = MaterialTheme.typography.bodyMedium
    )
    Text(
        text = "Из $count попыток Вы ошиблись $errors раз",
        style = MaterialTheme.typography.bodyMedium
    )
    Text(
        text = "Ваш уникальный id $id",
        style = MaterialTheme.typography.bodyMedium
    )
}