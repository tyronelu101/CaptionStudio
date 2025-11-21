package com.example.captionstudio.studio

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.captionstudio.Result
import com.example.captionstudio.components.CaptionItem
import com.example.captionstudio.components.StudioController
import com.example.captionstudio.components.StudioUIState
import com.example.captionstudio.domain.models.Transcription
import com.example.captionstudio.domain.recorder.Amplitude
import kotlinx.serialization.Serializable

@Serializable
data class StudioRoute(val transcriptionId: Long, val audioPath: String)

@Composable
fun StudioScreen(
    modifier: Modifier = Modifier,
    viewModel: StudioViewModel = hiltViewModel()
) {
    val transcription: Result<Transcription> by viewModel.transcription.collectAsStateWithLifecycle()
    val studioUIState: StudioUIState by viewModel.studioUIState.collectAsStateWithLifecycle()
    val captions: List<CaptionItemUI> by viewModel.captions.collectAsStateWithLifecycle()
    val amplitudes: List<Amplitude> by viewModel.amplitudes.collectAsStateWithLifecycle()
    val currentTime: Int by viewModel.currentTime.collectAsStateWithLifecycle()
    val duration: Long by viewModel.duration.collectAsStateWithLifecycle()

    if (transcription is Result.Success<Transcription>) {
        val audioUri = (transcription as Result.Success<Transcription>).data.audioURI
        StudioScreen(
            amplitudes,
            captions,
            studioUIState,
            currentTime,
            duration,
            startRecording = { viewModel.startRecording(audioUri) },
            pauseRecording = { viewModel.pauseRecording() },
            onPlay = { viewModel.startPlaying(audioUri) },
            onPause = { viewModel.pausePlaying() },
            onSeek = { time ->
                viewModel.seek(time)
            },
            onSeekFinished = {
            },
            modifier
        )
    } else {
        Text("Loading")
    }
}

@Composable
private fun StudioScreen(
    amplitudes: List<Amplitude>,
    captions: List<CaptionItemUI>,
    studioUIState: StudioUIState,
    currentTime: Int,
    duration: Long,
    startRecording: () -> Unit,
    pauseRecording: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    modifier: Modifier
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("RecordingScreen", "Permission granted")
            } else {
                Log.i("RecordingScreen", "Permission denied")
            }
        }

    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CaptionsList(
            captions = captions,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(80f)
        )
        StudioController(
            studioUIState = studioUIState,
            currentTime = currentTime,
            duration = duration,
            onStartRecording = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) -> {
                        startRecording()
                    }

                    else -> {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            onPauseRecording = pauseRecording,
            onPlay = onPlay,
            onPause = onPause,
            onSeek = onSeek,
            onSeekFinished = onSeekFinished,
            modifier = Modifier
                .fillMaxWidth()
                .weight(20f)
        )
    }
}

@Composable
private fun CaptionsList(captions: List<CaptionItemUI>, modifier: Modifier) {

    val captionsListState = rememberLazyListState()
    LaunchedEffect(captions) {
        captionsListState.animateScrollToItem(captions.size)
    }
    if (captions.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No captions yet")
            Text("Press the record button to start")
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = captionsListState
        ) {
            items(captions) { caption ->
                CaptionItem(caption)
            }
        }
    }

}

@Composable
private fun CaptionItem(caption: CaptionItemUI, modifier: Modifier = Modifier) {

    when (caption) {
        is CaptionItemUI.Listening -> {
            Text(
                modifier = modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth(1f),
                style = TextStyle(fontSize = 12.sp),
                textAlign = TextAlign.Center,
                text = "Listening..."
            )
        }

        is CaptionItemUI.CaptionItem -> {
            val words = caption.words.map { it.text }
            CaptionItem(
                text = words.joinToString(" "),
                startTime = caption.startTime,
                endTime = caption.endTime,
                onEdit = {}
            )
        }
    }
}

@Preview
@Composable
private fun CaptionItemPreview() {
//    val words = "This is an example of a sentence.".split(" ").map {
//        val confidence = 0.5f + Math.random().toFloat() * 0.5
//        Word(0L, it, confidence = confidence.toFloat(), 0L, 0L)
//    }
//    CaptionItem(
//        CaptionItemUI.CaptionItem("00:00", "00:00", words),
//        Modifier.background(Color.White)
//    )
}