package aboutuser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import data.model.dto.database.SessionDTO
import data.model.dto.test.TestMetric
import data.model.dto.test.TrialOutcome
import data.model.dto.test.TrialRecord
import data.model.regular.user.Handedness
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.tests.reactionTestName
import other.components.formatMetric
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import theme.ExtendedTheme
import utils.timeInMillis
import java.text.SimpleDateFormat
import java.util.*

/**
 * Карточка сессии. В раскрытом виде показывает все записанные данные
 * и график времени реакции по попыткам (загружается из БД при первом раскрытии).
 */
@Composable
fun SessionCard(
    sessionDTO: SessionDTO,
    loadTrials: suspend (sessionId: Long) -> List<TrialRecord>,
    loadMetrics: suspend (sessionId: Long) -> List<TestMetric>
) {
    var isExpanded by remember { mutableStateOf(false) }
    var trials by remember(sessionDTO.sessionId) { mutableStateOf<List<TrialRecord>?>(null) }
    var metrics by remember(sessionDTO.sessionId) { mutableStateOf<List<TestMetric>>(emptyList()) }

    LaunchedEffect(isExpanded) {
        if (isExpanded && trials == null) {
            try {
                withContext(Dispatchers.IO) {
                    metrics = loadMetrics(sessionDTO.sessionId)
                    trials = loadTrials(sessionDTO.sessionId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                trials = emptyList()
            }
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .border(
                width = if (isExpanded) 2.dp else 0.dp,
                color = if (isExpanded) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
        verticalArrangement = ExtensionPadding.mediumVerticalArrangementTop,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ExtensionPadding.mediumHorPadding,
                    vertical = ExtensionPadding.mediumVerPadding
                )
        ) {
            Text(
                text = "Сессия №${sessionDTO.sessionId} ${
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(sessionDTO.timeInMillis()))
                } с интервалом ${sessionDTO.signalInterval} секунд",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "Показать дополнительную информацию",
                    modifier = Modifier.rotate(rotation)
                )
            }
            Text(
                text = reactionTestName(sessionDTO.testId),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ExtensionPadding.mediumHorPadding,
                    vertical = ExtensionPadding.mediumVerPadding
                )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SessionDetails(sessionDTO = sessionDTO, trials = trials)
                if (metrics.isNotEmpty()) {
                    DetailsSection("Показатели теста") {
                        metrics.forEach { Line(it.name, formatMetric(it.value, it.unit)) }
                    }
                }
                TrialsChart(trials = trials)
            }
        }

        if (!isExpanded)
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .background(color = Color.Transparent, shape = MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                thickness = ConstantSize.dividerStatisticHeight
            )
    }
}

@Composable
private fun SessionDetails(sessionDTO: SessionDTO, trials: List<TrialRecord>?) = with(sessionDTO) {
    DetailsSection("Результат") {
        val correctRts = trials.orEmpty().filter { it.outcome == TrialOutcome.CORRECT }.mapNotNull { it.reactionTimeMs }
        Line("Среднее время правильной реакции", formatMs(averageValue))
        Line("Стандартное отклонение", formatMs(standardDeviation))
        if (correctRts.isNotEmpty()) Line("Медиана", formatMs(median(correctRts) / 1000))
        Line("Предъявлено сигналов", count.toString())
        validCount?.let { Line("Учтено правильных реакций", it.toString()) }
        Text(
            text = "Ошибок всего: $errors" + if (wrongResponses != null) {
                " (неверная реакция $wrongResponses, пропуск сигнала $omissions, фальстарт $prematureResponses)"
            } else "",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
        )
    }

    if (startedAt != null || finishedAt != null || withPractice != null) {
        DetailsSection("Проведение теста") {
            startedAt?.let { Line("Начало теста", it) }
            mainStartedAt?.takeIf { it != startedAt }?.let { Line("Начало зачётной части", it) }
            finishedAt?.let { Line("Окончание", it) }
            withPractice?.let { Line("Тренировка перед тестом", if (it) "была" else "пропущена") }
        }
    }

    DetailsSection("Водитель на момент теста") {
        Line("Категория водительского удостоверения", drivingLicenseCategory.toString())
        Line("Стаж вождения", "$experience ${yearsEnding(experience.toIntOrNull() ?: 0)}")
        Line("Возраст", userAge)
        if (handedness != Handedness.UNKNOWN) Line("Ведущая рука", handedness.toString())
        userSessionNumber?.let { Line("Прохождение этого теста", "$it-е") }
    }

    with(preTestState) {
        if (visionCorrection != null || sleepHours != null || wellBeing != null || responseHand != null || inputDevice != null) {
            DetailsSection("Состояние перед тестом") {
                visionCorrection?.let { Line("Коррекция зрения", it.toString()) }
                sleepHours?.let { Line("Сон за сутки", "$it ч") }
                wellBeing?.let { Line("Самочувствие (1–5)", it.toString()) }
                responseHand?.let { Line("Рука для ответа", it.toString()) }
                inputDevice?.let { Line("Устройство ввода", it.toString()) }
            }
        }
    }

    val env = environment
    if (env != null) {
        DetailsSection("Рабочее место") {
            if (env.monitorName.isNotEmpty()) Line("Монитор", env.monitorName)
            if (env.monitorSizeCm.isNotEmpty()) {
                Line(
                    "Размер изображения",
                    "${env.monitorSizeCm} см" + (env.monitorDiagonalInches?.let { ", диагональ $it″" } ?: "")
                )
            }
            if (env.screenResolution.isNotEmpty()) {
                Line("Разрешение", env.screenResolution + (env.refreshRateHz?.let { ", $it Гц" } ?: ""))
            }
            env.scalePercent?.let { Line("Масштаб ОС", "$it %") }
            if (env.testWindowPx.isNotEmpty()) Line("Окно теста", "${env.testWindowPx} px")
            if (env.computerName.isNotEmpty()) Line("Компьютер", env.computerName)
            if (env.operatingSystem.isNotEmpty()) Line("ОС", env.operatingSystem)
            Line("Версия программы / методики", "${env.appVersion} / ${env.methodVersion}")
        }
    } else {
        Text(
            text = "Сессия записана по методике версии 1: подробные данные не сохранялись.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Время правильных реакций по номеру попытки. */
@Composable
private fun TrialsChart(trials: List<TrialRecord>?) {
    when {
        trials == null -> CircularProgressIndicator()
        trials.isEmpty() -> Text(
            text = "Попытки этой сессии не записаны.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        else -> {
            val correct = trials.filter { it.outcome == TrialOutcome.CORRECT && it.reactionTimeMs != null }
            DetailsSection("Попытки") {
                trials.groupingBy { it.outcome }.eachCount().forEach { (outcome, number) ->
                    Line(outcome.title.replaceFirstChar { it.uppercase() }, number.toString())
                }
            }
            if (correct.size >= 2) {
                Box(modifier = Modifier.height(320.dp)) {
                    LineChart(
                        modifier = Modifier.fillMaxWidth(),
                        linesParameters = listOf(
                            LineParameters(
                                label = "Время правильной реакции по попыткам, мс",
                                data = correct.map { it.reactionTimeMs!! },
                                lineColor = ExtendedTheme.colors.lineChartColor1,
                                lineType = LineType.DEFAULT_LINE,
                                lineShadow = false,
                            )
                        ),
                        isGrid = true,
                        gridColor = ExtendedTheme.colors.lineChartGripColor,
                        xAxisData = correct.map { it.number.toString() },
                        animateChart = false,
                        showGridWithSpacer = true,
                        yAxisStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                        xAxisStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                        yAxisRange = 6,
                        oneLineChart = false,
                        gridOrientation = GridOrientation.GRID,
                        showXAxis = true,
                        showYAxis = true,
                        legendPosition = LegendPosition.BOTTOM,
                        descriptionStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun Line(label: String, value: String) {
    Text(text = "$label: $value", style = MaterialTheme.typography.bodyMedium)
}

private fun formatMs(seconds: Double): String = String.format(Locale.ROOT, "%.0f мс", seconds * 1000)

private fun median(values: List<Double>): Double {
    val sorted = values.sorted()
    val middle = sorted.size / 2
    return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
}

private fun yearsEnding(years: Int): String {
    val lastTwo = years % 100
    val last = years % 10
    return when {
        lastTwo in 11..14 -> "лет"
        last == 1 -> "год"
        last in 2..4 -> "года"
        else -> "лет"
    }
}
