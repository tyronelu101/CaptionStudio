package com.example.captionstudio.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.captionstudio.app.toPx
import com.example.captionstudio.domain.recorder.Amplitude
import kotlin.math.abs
import kotlin.math.roundToInt

private val MAX_WAVE_BAR_HEIGHT = 80.dp
private val MIN_WAVE_BAR_HEIGHT = 8.dp
private val BAR_WIDTH = 3.dp
private val GAP_BETWEEN_BARS = BAR_WIDTH + 4.dp

private val TICKER_HEIGHT = 24.dp

private val PADDING = 56.dp

@Composable
fun AudioWaveVisualizer(
    allowSeek: Boolean,
    amplitudes: List<Amplitude>,
    modifier: Modifier = Modifier
) {

    val configuration = LocalConfiguration.current
    val screenWidthDp: Dp = configuration.screenWidthDp.dp
    val center = screenWidthDp / 2

    var offset by remember { mutableFloatStateOf(0f) }

    var scrollTick = 0f
    val scrollThreshold = GAP_BETWEEN_BARS.toPx()
    val width = amplitudes.size * GAP_BETWEEN_BARS.toPx()
    val brush = Brush.verticalGradient(listOf(Color.Black, Color.Red, Color.Black))

    Box(
        modifier = modifier
            .clipToBounds()
            .fillMaxSize()
            .then(
                if (allowSeek) modifier.scrollable(
                    orientation = Orientation.Horizontal,
                    state = rememberScrollableState { delta ->
                        scrollTick += delta

                        if ((offset in 0f..width) && abs(scrollTick) >= scrollThreshold) {
                            offset = if (scrollTick < 0) {
                                maxOf(offset - scrollThreshold, 0f)
                            } else {
                                minOf(offset + scrollThreshold, width)
                            }
                            scrollTick = 0f
                        }
                        delta
                    }) else modifier
            )
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(bottom = PADDING)

        ) {
            translate(
                if (allowSeek) offset
                else {
                    offset = 0f
                    0f
                }
            ) {
                //Handle auto scrolling
                val translate =
                    center.toPx() - (GAP_BETWEEN_BARS.toPx() * amplitudes.size) - BAR_WIDTH.toPx() / 2f
                withTransform({ translate(translate) }) {
                    var offSetX = 0f
                    var countTick = 0
                    for (amplitude in amplitudes) {
                        val waveHeight =
                            if (amplitude.amplitude < 0.1f) MIN_WAVE_BAR_HEIGHT else MAX_WAVE_BAR_HEIGHT * amplitude.amplitude
                        val offsetY = (size.height - waveHeight.toPx()) / 2
                        drawRoundRect(
                            Color.White,
                            topLeft = Offset(offSetX, offsetY),
                            size = Size(BAR_WIDTH.toPx(), waveHeight.toPx()),
                            cornerRadius = CornerRadius(x = 48f, y = 48f)
                        )

//                        if (countTick % 3 == 0) {
//                            Log.i("Test", "Amplitude data is ${amplitude.time}")
//                            drawLine( 
//                                Color.White,
//                                strokeWidth = 5f,
//                                start = Offset(
//                                    x = offSetX + BAR_WIDTH.toPx() / 2,
//                                    y = offsetY + waveHeight.toPx() + 50
//                                ),
//                                end = Offset(
//                                    x = offSetX + BAR_WIDTH.toPx() / 2,
//                                    offsetY + waveHeight.toPx() + TICKER_HEIGHT.toPx()
//                                )
//                            )
//                        }
                        ++countTick

                        offSetX += GAP_BETWEEN_BARS.toPx()
                    }
                }

                //Right side empty static wave bars
                var currentOffsetX = center.toPx() - (BAR_WIDTH.toPx() / 2f)

                //draw empty wave form bars on right side of vertical line
                val availableSpace =
                    ((screenWidthDp.toPx() - GAP_BETWEEN_BARS.toPx()) / 2f) / (GAP_BETWEEN_BARS.toPx() + BAR_WIDTH.toPx())
                val count = (screenWidthDp.toPx() / 2f) / (availableSpace)
                val offsetY = (size.height - MIN_WAVE_BAR_HEIGHT.toPx()) / 2
                for (i in 0..count.roundToInt()) {
                    drawRoundRect(
                        Color.LightGray,
                        topLeft = Offset(currentOffsetX, offsetY),
                        size = Size(BAR_WIDTH.toPx(), MIN_WAVE_BAR_HEIGHT.toPx()),
                        cornerRadius = CornerRadius(x = 48f, y = 48f)
                    )
                    currentOffsetX += GAP_BETWEEN_BARS.toPx()
                }

            }

            //Draw vertical line
            drawLine(
                brush = brush,
                strokeWidth = 1.dp.toPx(),
                start = Offset(x = center.toPx(), y = 0f),
                end = Offset(x = center.toPx(), size.height)
            )
        }
    }
}