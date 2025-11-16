package com.example.captionstudio.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.captionstudio.util.TimeUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(
    currentTime: Int,
    maxTime: Long,
    onSeek: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    playbackUiState: StudioUIState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (maxTime == 0L) "00:00" else if (playbackUiState == StudioUIState.Recording) TimeUtil.convertSecondsToTimeString(
                maxTime.toInt()
            ) else {
                "${TimeUtil.convertSecondsToTimeString(currentTime.toInt())} / ${
                    TimeUtil.convertSecondsToTimeString(
                        maxTime.toInt()
                    )
                }"
            },
        )
        Slider(
            valueRange = 0F..maxTime.toFloat(),
            value = currentTime.toFloat(),
            onValueChange = {
                onSeek(it.toInt())
            },
            onValueChangeFinished = onSeekFinished,
            enabled = maxTime > 0 && (playbackUiState == StudioUIState.Paused || playbackUiState == StudioUIState.Playing),
            track = {
                SliderDefaults.Track(
                    modifier = Modifier.fillMaxHeight(0.10f), colors = SliderDefaults.colors(
                    ), sliderState = it
                )
            },
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 500)
private fun SliderPreview() {
    SeekBar(30_000, 90_000, {}, {}, StudioUIState.Paused)
}