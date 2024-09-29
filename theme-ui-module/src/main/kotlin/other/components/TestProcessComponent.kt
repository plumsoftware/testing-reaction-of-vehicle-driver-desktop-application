package other.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import other.extension.size.ConstantSize

@Composable
fun TestProccessComponent(userClicked: Int, count: Int, currentLampIndex: Int) {
    Box {
        if (userClicked != count) {
            Image(painter = painterResource("traffic_light.png"), contentDescription = "Изображение светофора")
//                region::Lamps
            Box(
                modifier = Modifier
                    .padding(top = 53.dp)
                    .clip(shape = RoundedCornerShape(200.dp))
                    .align(Alignment.TopCenter)
                    .background(if (currentLampIndex == 0) Color.Red else Color.Gray)
                    .size(ConstantSize.trafficLightLampSize)
            )

            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .clip(shape = RoundedCornerShape(200.dp))
                    .align(Alignment.Center)
                    .background(if (currentLampIndex == 1) Color.Yellow else Color.Gray)
                    .size(ConstantSize.trafficLightLampSize)
            )

            Box(
                modifier = Modifier
                    .padding(bottom = 110.dp)
                    .clip(shape = RoundedCornerShape(200.dp))
                    .align(Alignment.BottomCenter)
                    .background(if (currentLampIndex == 2) Color.Green else Color.Gray)
                    .size(ConstantSize.trafficLightLampSize)
            )
//                endregion
        }
    }
}