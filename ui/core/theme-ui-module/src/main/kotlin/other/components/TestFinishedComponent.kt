package other.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import java.util.Locale

/** Состояние сохранения результатов теста. */
enum class SaveStatus(val text: String) {
    SAVING("Сохранение результатов…"),
    SAVED("Результаты сохранены"),
    FAILED("Не удалось сохранить результаты")
}

/**
 * Итоги теста. Время показывается в миллисекундах, в БД и Excel хранится в секундах.
 */
@Composable
fun TestFinishedComponent(
    result: TestDTO,
    saveStatus: SaveStatus,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = modifier.widthIn(max = 720.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Тестирование окончено", style = MaterialTheme.typography.headlineMedium)

            val n = result.reactionTimesMs.size
            if (n > 0) {
                ResultLine("Среднее время правильной реакции", formatMs(result.averageValue))
                ResultLine(
                    "Стандартное отклонение",
                    if (n >= 2) formatMs(result.stdDeviationValue) else "— (нужно ≥ 2 реакций)"
                )
            } else {
                ResultLine("Среднее время правильной реакции", "нет правильных реакций")
            }
            ResultLine("Предъявлено сигналов", result.count.toString())
            ResultLine("Учтено правильных реакций", n.toString())

            HorizontalDivider()

            ResultLine("Ошибок всего", (result.errorsCount ?: 0).toString())
            ResultLine("— неверная реакция", result.wrongResponses.toString())
            ResultLine("— пропуск сигнала", result.omissions.toString())
            ResultLine("— фальстарт", result.prematureResponses.toString())

            HorizontalDivider()

            ResultLine("Ваш уникальный id", result.user.id.toString())
            Text(
                text = saveStatus.text,
                style = MaterialTheme.typography.bodyMedium,
                color = when (saveStatus) {
                    SaveStatus.SAVING -> MaterialTheme.colorScheme.onSurfaceVariant
                    SaveStatus.SAVED -> MaterialTheme.colorScheme.primary
                    SaveStatus.FAILED -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun formatMs(seconds: Double): String = String.format(Locale.ROOT, "%.0f мс", seconds * 1000)
