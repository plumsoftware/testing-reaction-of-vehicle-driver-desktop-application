package other.components.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import other.extension.size.ConstantSize
import theme.trafficLightBranchColor
import theme.trafficLightColor

@Composable
fun SmallTrafficLightTopSideView(
    currentLampIndex: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(color = trafficLightColor)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(ConstantSize.Road.trafficLight)
                    .clip(shape = RoundedCornerShape(100.dp))
                    .background(if (currentLampIndex == 0) Color.Red else Color.Gray)
            )
            Spacer(modifier = Modifier.height(height = 6.dp))
            Box(
                modifier = Modifier
                    .size(ConstantSize.Road.trafficLight)
                    .clip(shape = RoundedCornerShape(100.dp))
                    .background(if (currentLampIndex == 1) Color.Yellow else Color.Gray)
            )
            Spacer(modifier = Modifier.height(height = 6.dp))
            Box(
                modifier = Modifier
                    .size(ConstantSize.Road.trafficLight)
                    .clip(shape = RoundedCornerShape(100.dp))
                    .background(if (currentLampIndex == 2) Color.Green else Color.Gray)
            )
        }
        Box(
            modifier = Modifier
                .size(ConstantSize.Road.trafficLightBranchSize)
                .background(color = trafficLightBranchColor)
        )
    }
}
