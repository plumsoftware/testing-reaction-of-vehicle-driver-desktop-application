package other.components

import androidx.compose.runtime.Composable
import other.components.test.TrafficLight

@Composable
fun TestProccessComponent(userClicked: Int, count: Int, currentLampIndex: Int) {
    if (userClicked != count) {
        TrafficLight(currentLampIndex = currentLampIndex)
    }
}