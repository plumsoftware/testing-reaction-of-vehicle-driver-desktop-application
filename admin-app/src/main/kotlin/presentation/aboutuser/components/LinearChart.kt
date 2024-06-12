package presentation.aboutuser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import domain.model.regular.userstatistic.LineChartItem
import presentation.theme.ExtendedTheme
import utils.toSemiDate
import utils.x
import utils.y

@Composable
fun LinearChart(list: List<LineChartItem>) {
    val testLineParameters: List<LineParameters> = listOf(
        LineParameters(
            label = "Среднее значение",
            data = list.y(),
            lineColor = ExtendedTheme.colors.lineChartColor1,
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false,
        )
    )
    Box(modifier = Modifier.size(width = 400.dp, height = 550.dp)) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            linesParameters = testLineParameters,
            isGrid = true,
            gridColor = ExtendedTheme.colors.lineChartGripColor,
            xAxisData = list.x().toSemiDate(),
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
            descriptionStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),

        )
    }
}