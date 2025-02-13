package other.extension.size

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object ConstantSize {
    val homeButtonWidth = 240.dp

    val regularBorderWidth = 2.dp

    val trafficLightBoxSize = DpSize(width = 120.dp, height = 120.dp)
    val trafficLightPaddingHor = 46.dp
    val trafficLightPaddingVer = 32.dp
    val trafficLightLampSize = DpSize(80.dp, 80.dp)
    val trafficLightLampButtonSize = DpSize(200.dp, 80.dp)

    val dividerStatisticHeight = 4.dp
    val emptySessionsListHeight = 100.dp
    val emptySessionsListSize = DpSize(width = 74.dp, height = 74.dp)

    object Road {
        val roadSideWidth = 240.dp
        val sky1Height = 140.dp
        val solidWhiteLineHeight = 10.dp
        val roadMarkerSize = DpSize(width = 20.dp, height = 10.dp)
        val roadMarkerWidth = 10.dp

        val trafficLight = DpSize(50.dp, 50.dp)

        val trafficLightBranchSize = DpSize(width = 20.dp, height = 50.dp)
    }

    object Car {
        val trafficLightTestByRoadWithRedLightCarTopSideViewSize = DpSize(width = 50.dp, height = 50.dp)
    }
}