package com.example.captionstudio.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.captionstudio.R
import com.example.captionstudio.app.ui.CaptionStudioIcons

sealed interface StudioUIState {
    //First state when nothing has been
    data object Loading : StudioUIState
    data object Paused : StudioUIState
    data object Recording : StudioUIState
    data object Playing : StudioUIState

}

@Composable
fun StudioController(
    studioUIState: StudioUIState,
    currentTime: Int,
    duration: Long,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        SeekBar(
            currentTime = currentTime,
            maxTime = duration,
            onSeek,
            onSeekFinished,
            studioUIState,
            modifier = Modifier
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            RecordButton(
                isRecording = studioUIState is StudioUIState.Recording,
                isEnabled = studioUIState is StudioUIState.Paused || studioUIState is StudioUIState.Recording,
                onRecord = onStartRecording,
                onPause = onPauseRecording,
                modifier = modifier
            )
            PlaybackButton(
                isPlaying = studioUIState is StudioUIState.Playing,
                isEnabled = duration > 0 && (studioUIState is StudioUIState.Paused || studioUIState is StudioUIState.Playing),
                onPlay = onPlay,
                onPause = onPause,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun RecordButton(
    isRecording: Boolean,
    isEnabled: Boolean,
    onRecord: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier
) {
    Button(
        enabled = isEnabled,
        onClick = {
            if (isRecording) onPause() else onRecord()
        },
    ) {
        Text(text = if (isRecording) "Pause" else "Record")
    }
}


@Composable
private fun PlaybackButton(
    isPlaying: Boolean,
    isEnabled: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        enabled = isEnabled,
        onClick = {
            if (isPlaying) onPause() else onPlay()
        }) {
        Icon(
            imageVector = if (isPlaying) CaptionStudioIcons.PAUSE else CaptionStudioIcons.PLAY,
            contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play)
        )
    }
}

@Composable
private fun AudioControlButton(
    iconResource: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            painter = painterResource(
                id = iconResource
            ),
            contentDescription = contentDescription
        )
    }
}

//@Composable
//@Preview(showBackground = true)
//private fun StudioControllerPreview() {
//    StudioController(
//        studioUIState = StudioUIState.Recording,
//        duration = 65L,
//        onStartRecording = {},
//        onPauseRecording = {},
//        onPlay = {},
//        onPause = {},
//        modifier = Modifier
//    )
//}
