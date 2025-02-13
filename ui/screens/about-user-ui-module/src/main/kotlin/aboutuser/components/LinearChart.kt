package aboutuser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import data.model.dto.database.SessionDTO
import theme.ExtendedTheme
import utils.toLineChartData
import utils.toLineChartData1
import utils.toLineChartData2
import utils.toLineChartData3
import utils.toSemiDate
import utils.x
import utils.y

@Composable
fun LinearChart(list: List<SessionDTO>, acceptedLineParams: List<Boolean>) {

    val lineParameters: MutableList<LineParameters> = mutableListOf(
        LineParameters(
            label = "Среднее значение",
            data = list.toLineChartData().y(),
            lineColor = ExtendedTheme.colors.lineChartColor1,
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false,
        ),
        LineParameters(
            label = "Стандартное отклонение",
            data = list.toLineChartData1().y(),
            lineColor = ExtendedTheme.colors.lineChartColor2,
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false,
        ),
        LineParameters(
            label = "Количество ошибок",
            data = list.toLineChartData2().y(),
            lineColor = MaterialTheme.colorScheme.error,
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false,
        ),
        LineParameters(
            label = "Количество попыток",
            data = list.toLineChartData3().y(),
            lineColor = ExtendedTheme.colors.onSuccessContainer,
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false,
        )
    )

    val lineParametersToShow = mutableListOf<LineParameters>()

    acceptedLineParams.forEachIndexed { index, item ->
        if (item) {
            lineParametersToShow.add(lineParameters[index])
        }
    }

    Box(modifier = Modifier.height(height = 550.dp)) {
        LineChart(
            modifier = Modifier.fillMaxWidth(),
            linesParameters = lineParametersToShow,
            isGrid = true,
            gridColor = ExtendedTheme.colors.lineChartGripColor,
            xAxisData = list.toLineChartData().x().toSemiDate(),
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