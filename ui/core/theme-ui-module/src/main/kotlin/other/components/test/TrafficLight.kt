package other.components.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
fun TrafficLight(currentLampIndex: Int) {
    val roundBox = 22.dp
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentHeight().fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(topStart = roundBox, topEnd = roundBox))
                .background(color = trafficLightColor)
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = ConstantSize.trafficLightPaddingHor,
                        end = ConstantSize.trafficLightPaddingHor,
                        top = ConstantSize.trafficLightPaddingVer
                    )
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(200.dp))
                        .align(Alignment.Center)
                        .background(if (currentLampIndex == 0) Color.Red else Color.Gray)
                        .size(ConstantSize.trafficLightLampSize)
                )
            }
        }
        Box(
            modifier = Modifier
                .background(color = trafficLightColor)
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = ConstantSize.trafficLightPaddingHor,
                        vertical = ConstantSize.trafficLightPaddingVer
                    )
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(200.dp))
                        .align(Alignment.Center)
                        .background(if (currentLampIndex == 1) Color.Yellow else Color.Gray)
                        .size(ConstantSize.trafficLightLampSize)
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(bottomEnd = roundBox, bottomStart = roundBox))
                .background(color = trafficLightColor)
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = ConstantSize.trafficLightPaddingHor,
                        end = ConstantSize.trafficLightPaddingHor,
                        bottom = ConstantSize.trafficLightPaddingVer
                    )
                    .background(color = trafficLightColor)
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(200.dp))
                        .align(Alignment.BottomCenter)
                        .background(if (currentLampIndex == 2) Color.Green else Color.Gray)
                        .size(ConstantSize.trafficLightLampSize)
                )
            }
        }
        Box(
            modifier = Modifier.width(width = 40.dp).height(height = 80.dp)
                .background(color = trafficLightBranchColor)
        )
    }
}
