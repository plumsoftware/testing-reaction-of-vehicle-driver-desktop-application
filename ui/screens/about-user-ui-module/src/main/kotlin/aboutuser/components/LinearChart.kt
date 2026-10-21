package aboutuser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import data.constant.UIConstants
import data.model.dto.database.SessionDTO
import theme.ExtendedTheme
import utils.toLineChartData
import utils.toSemiDate
import utils.x
import utils.y

/**
 * Динамика по сессиям. Линии и их порядок соответствуют [UIConstants.lineChartParams].
 * Время реакции показывается в мс, чтобы быть в одном масштабе со счётчиками.
 * Для сессий до версии методики 2 разбивка ошибок не записывалась и показывается как 0.
 */
@Composable
fun LinearChart(list: List<SessionDTO>, acceptedLineParams: List<Boolean>) {

    val series: List<Pair<Color, (SessionDTO) -> Double>> = listOf(
        ExtendedTheme.colors.lineChartColor1 to { it.averageValue * 1000 },
        ExtendedTheme.colors.lineChartColor2 to { it.standardDeviation * 1000 },
        MaterialTheme.colorScheme.error to { it.errors.toDouble() },
        ExtendedTheme.colors.onSuccessContainer to { it.count.toDouble() },
        MaterialTheme.colorScheme.tertiary to { (it.wrongResponses ?: 0).toDouble() },
        MaterialTheme.colorScheme.secondary to { (it.omissions ?: 0).toDouble() },
        MaterialTheme.colorScheme.primary to { (it.prematureResponses ?: 0).toDouble() },
    )

    val lineParametersToShow = series.mapIndexedNotNull { index, (color, value) ->
        if (acceptedLineParams.getOrElse(index) { false }) {
            LineParameters(
                label = UIConstants.lineChartParams[index],
                data = list.toLineChartData(value).y(),
                lineColor = color,
                lineType = LineType.DEFAULT_LINE,
                lineShadow = false,
            )
        } else null
    }

    if (lineParametersToShow.isEmpty()) {
        Text(
            text = "Выберите хотя бы один показатель под графиком",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    // Библиотека графиков запоминает данные при первом показе — пересоздаём график при их смене
    key(lineParametersToShow.map { it.label }, list) {
        Box(modifier = Modifier.height(height = 550.dp)) {
            LineChart(
                modifier = Modifier.fillMaxWidth(),
                linesParameters = lineParametersToShow,
                isGrid = true,
                gridColor = ExtendedTheme.colors.lineChartGripColor,
                xAxisData = list.toLineChartData { 0.0 }.x().toSemiDate(),
                animateChart = true,
                showGridWithSpacer = true,
                yAxisStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                xAxisStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                yAxisRange = 10,
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
