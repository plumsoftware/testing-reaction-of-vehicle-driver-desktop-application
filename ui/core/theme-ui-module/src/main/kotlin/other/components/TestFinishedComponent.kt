package other.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import data.model.dto.test.TestDTO
import data.model.dto.test.TrialOutcome
import java.awt.Desktop
import java.io.File
import java.util.Locale

/** Состояние сохранения результатов теста. */
enum class SaveStatus(val text: String) {
    SAVING("Сохранение результатов…"),
    SAVED("Результаты сохранены"),
    FAILED("Не удалось сохранить результаты")
}

/** Ключи показателей с числом ошибок (см. errorCountMetrics в tests-ui-module). */
private val ERROR_METRIC_KEYS = setOf("errors_total", "wrong_responses", "omissions", "false_starts", "anticipations")

/** Подписи основных показателей; у РДО вместо времени реакции — ошибка остановки. */
data class ResultLabels(
    val average: String = "Среднее время правильной реакции",
    val standardDeviation: String = "Стандартное отклонение",
    val validCount: String = "Учтено правильных реакций",
)

/**
 * Итоги теста. Время показывается в миллисекундах, в БД и Excel хранится в секундах.
 * Текст можно выделить и скопировать; путь к Excel-файлу по клику показывает файл в Finder / Проводнике.
 */
@Composable
fun TestFinishedComponent(
    result: TestDTO,
    saveStatus: SaveStatus,
    resultFiles: List<File>,
    modifier: Modifier = Modifier,
    labels: ResultLabels = ResultLabels()
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = modifier.widthIn(max = 720.dp)
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Тестирование окончено", style = MaterialTheme.typography.headlineMedium)

                val n = result.reactionTimesMs.size
                if (n > 0) {
                    ResultLine(labels.average, formatMs(result.averageValue))
                    ResultLine(
                        labels.standardDeviation,
                        if (n >= 2) formatMs(result.stdDeviationValue) else "— (нужно ≥ 2 значений)"
                    )
                } else {
                    ResultLine(labels.average, "нет учтённых ответов")
                }
                ResultLine("Предъявлено сигналов", result.count.toString())
                ResultLine(labels.validCount, n.toString())

                if (result.metrics.any { it.key !in ERROR_METRIC_KEYS }) {
                    HorizontalDivider()
                    // Ошибки по видам показаны ниже отдельным блоком
                result.metrics.filter { it.key !in ERROR_METRIC_KEYS }.forEach { metric ->
                        ResultLine(metric.name, formatMetric(metric.value, metric.unit))
                    }
                }

                HorizontalDivider()

                ResultLine("Ошибок всего", (result.errorsCount ?: 0).toString())
                ResultLine("— неверная реакция", result.wrongResponses.toString())
                ResultLine("— пропуск сигнала", result.omissions.toString())
                val falseStarts = result.trials.count { it.outcome == TrialOutcome.FALSE_START }
                val anticipations = result.trials.count { it.outcome == TrialOutcome.ANTICIPATION }
                ResultLine("— раннее нажатие (до сигнала)", falseStarts.toString())
                ResultLine("— слишком быстрый ответ (< 100 мс после сигнала)", anticipations.toString())

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
                if (saveStatus == SaveStatus.SAVED) {
                    resultFiles.forEach { ResultFileLink(file = it.absoluteFile) }
                }
            }
        }
    }
}

@Composable
private fun ResultFileLink(file: File) {
    val linkStyle = TextLinkStyles(
        style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)
    )
    Text(
        text = buildAnnotatedString {
            append("Таблица Excel: ")
            withLink(LinkAnnotation.Clickable(tag = "result_file", styles = linkStyle) { revealFile(file) }) {
                append(file.path)
            }
        },
        style = MaterialTheme.typography.bodyMedium
    )
}

/** Показывает файл в Проводнике / Finder (с выделением); на других ОС открывает его папку. */
private fun revealFile(file: File) {
    val os = System.getProperty("os.name")
    runCatching {
        when {
            os.startsWith("Windows", ignoreCase = true) ->
                ProcessBuilder("explorer.exe", "/select,", file.path).start()

            os.startsWith("Mac", ignoreCase = true) -> ProcessBuilder("open", "-R", file.path).start()

            else -> file.parentFile?.let { Desktop.getDesktop().open(it) }
        }
    }.onFailure { it.printStackTrace() }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium
    )
}

/** Целые величины — без дробной части, остальные — с точностью по смыслу единицы. */
fun formatMetric(value: Double, unit: String): String {
    val number = when {
        unit == "шт." || unit == "мин" -> String.format(Locale.ROOT, "%.0f", value)
        unit == "мс" || unit == "%" -> String.format(Locale.ROOT, "%.1f", value)
        else -> String.format(Locale.ROOT, "%.2f", value)
    }
    return if (unit.isEmpty()) number else "$number $unit"
}

private fun formatMs(seconds: Double): String = String.format(Locale.ROOT, "%.0f мс", seconds * 1000)
