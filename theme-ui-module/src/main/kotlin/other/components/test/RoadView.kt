package other.components.test

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import other.extension.padding.ExtensionPadding
import other.extension.size.ConstantSize

@Composable
fun RoadView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Дорога
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Обочина слева
            Box(
                modifier = Modifier
                    .width(ConstantSize.Road.roadSideWidth)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            )

            //Road
            Row(
                modifier = Modifier
                    .padding(top = ExtensionPadding.Road.roadMarkerPaddingTop)
                    .weight(1f)
                    .background(Color.LightGray)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                //Left line
                Box(modifier = Modifier.weight(0.5f)) {
//                    Row(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
//                            Box(modifier = Modifier)
                        Column(modifier = Modifier.fillMaxHeight()) {
                            repeat(50) {
                                Box(
                                    modifier = Modifier.size(ConstantSize.Road.roadMarkerSize)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.height(height = 10.dp))
                            }
                        }
//                        Box(modifier = Modifier.weight(0.5f))
                    }
//                    }
                }

                //Two solid
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(ConstantSize.Road.roadMarkerWidth)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }

                //Right line
                Box(modifier = Modifier.weight(0.5f)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(ConstantSize.Road.solidWhiteLineHeight)
                                .background(Color.White)
                        )
                        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                            Box(modifier = Modifier.weight(0.5f))
                            Column(modifier = Modifier.fillMaxHeight()) {
                                repeat(50) {
                                    Box(
                                        modifier = Modifier.size(ConstantSize.Road.roadMarkerSize)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.height(height = 10.dp))
                                }
                            }
                            Box(modifier = Modifier.weight(0.5f))
                        }
                    }
                }
            }


            // Обочина справа
            Box(
                modifier = Modifier
                    .width(ConstantSize.Road.roadSideWidth)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
            )
        }
    }
}

@Preview
@Composable
private fun RoadViewPreview() {
    RoadView()
}