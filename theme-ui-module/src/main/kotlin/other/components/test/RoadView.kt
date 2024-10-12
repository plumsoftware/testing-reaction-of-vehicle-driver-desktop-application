package other.components.test

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize
import theme.RoadColor


@Composable
fun RoadView(onClick: () -> Unit, animationPlaying: Boolean) {

    val animation = rememberInfiniteTransition()
    val progress by animation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = if (animationPlaying) {
            infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
                ),
                repeatMode = RepeatMode.Restart
            )
        } else {
            infiniteRepeatable(
                tween(durationMillis = 500, easing = CubicBezierEasing(0f, 0f, 0.2f, 1f))
            )
        }
    )

    val imageX = progress.times(1000f).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Дорога
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Обочина слева
            Box(
                modifier = Modifier
                    .height(ConstantSize.Road.roadSideWidth)
                    .fillMaxWidth()
                    .background(RoadColor.roadSide)
            ) {
                //Traffic light
//                SmallTrafficLightTopSideView(currentLampIndex = currentLampIndex)
            }

            //Road
            Column(
                modifier = Modifier
                    .padding(start = ExtensionPadding.Road.roadMarkerPaddingTop)
                    .weight(1f)
                    .background(Color.LightGray)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                //Road line
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                            .width(ConstantSize.Road.solidWhiteLineHeight)
                            .background(Color.White)
                    )
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxWidth()
                                .padding(vertical = ExtensionPadding.Road.carTopSideViewPadding)
                        ) {
                            //For car
                            Image(
                                painter = painterResource("car_top_side_view.png"),
                                contentDescription = "Машина на дороге",
                                modifier = Modifier
                                    .offset(x = imageX.times(-1))
                                    .wrapContentSize()
                                    .align(Alignment.CenterEnd)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            repeat(100) {
                                Box(
                                    modifier = Modifier.size(ConstantSize.Road.roadMarkerSize)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(width = 10.dp))
                            }
                        }
                        Box(modifier = Modifier.weight(0.5f))
                    }
                }
            }

            // Обочина справа
            Box(
                modifier = Modifier
                    .height(ConstantSize.Road.roadSideWidth)
                    .fillMaxWidth()
                    .background(RoadColor.roadSide)
            ) {
                StopPedalButton(onClick = onClick, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview
@Composable
private fun RoadViewPreview() {
    RoadView({}, animationPlaying = false)
}