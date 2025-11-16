package com.example.captionstudio.studio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.captionstudio.Result
import com.example.captionstudio.components.StudioUIState
import com.example.captionstudio.data.TranscriptionRepository
import com.example.captionstudio.domain.models.Caption
import com.example.captionstudio.domain.models.Transcription
import com.example.captionstudio.domain.models.Word
import com.example.captionstudio.domain.player.AudioPlayerFactory
import com.example.captionstudio.domain.player.AudioPlayerListener
import com.example.captionstudio.domain.recorder.Amplitude
import com.example.captionstudio.domain.recorder.AudioRecorderFactory
import com.example.captionstudio.domain.transcriber.Transcriber
import com.example.captionstudio.domain.transcriber.TranscriberResult
import com.example.captionstudio.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CaptionItemUI {
    data class Listening(val time: String) : CaptionItemUI
    data class CaptionItem(val startTime: String, val endTime: String, val words: List<Word>) :
        CaptionItemUI
}

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val audioRecorderFactory: AudioRecorderFactory,
    private val audioPlayerFactory: AudioPlayerFactory,
    private val savedStateHandle: SavedStateHandle,
    private val googleTranscriber: Transcriber,
    private val transcriptionRepository: TranscriptionRepository,
) : ViewModel() {

    private val studioParams = savedStateHandle.toRoute<StudioRoute>()
    private val audioRecorder = audioRecorderFactory.create(studioParams.audioPath)
    private val audioPlayer = audioPlayerFactory.create(studioParams.audioPath)

    private val _studioUIState: MutableStateFlow<StudioUIState> =
        MutableStateFlow(StudioUIState.Loading)
    val studioUIState: StateFlow<StudioUIState> =
        _studioUIState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StudioUIState.Loading
        )

    private val _amplitudes: MutableStateFlow<List<Amplitude>> = MutableStateFlow(emptyList())
    val amplitudes: StateFlow<List<Amplitude>> = _amplitudes

    private val _transcription: MutableStateFlow<Result<Transcription>> =
        MutableStateFlow(Result.Loading)
    val transcription: StateFlow<Result<Transcription>> = _transcription

    val captions: StateFlow<List<CaptionItemUI>> =
        transcriptionRepository.getCaptionsForTranscription(studioParams.transcriptionId)
            .map { captions ->
                captions.map { caption ->
                    CaptionItemUI.CaptionItem(
                        TimeUtil.convertMillisToTimeString(caption.startingTime),
                        TimeUtil.convertMillisToTimeString(caption.endingTime),
                        caption.words
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5000)
            )

    private val _currentTime: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime

    private val _duration: MutableStateFlow<Long> = MutableStateFlow(audioRecorder.getDuration())
    val duration: StateFlow<Long> = _duration

    private var previousPlaybackState: StudioUIState = studioUIState.value

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _transcription.value =
                Result.Success(transcriptionRepository.getTranscription(studioParams.transcriptionId))
            _studioUIState.value = StudioUIState.Paused
        }

        audioPlayer.setOnProgressListener(object : AudioPlayerListener {
            override fun onProgress(time: Int) {
                _currentTime.value = time
            }

            override fun onFinish() {
                _studioUIState.value = StudioUIState.Paused
            }
        })
    }

    fun startRecording(filePath: String) {
        _studioUIState.value = StudioUIState.Recording

        viewModelScope.launch(Dispatchers.IO) {
            googleTranscriber.resultFlow.collect {
                handleTranscriberResult(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            audioRecorder.start(filePath).collect {
                _duration.value = audioRecorder.getDuration()
                googleTranscriber.transcribe(it.toByteArray())
            }
        }
    }

    private fun handleTranscriberResult(result: TranscriberResult) {
        if (result is TranscriberResult.FinalResult) {
            val words = result.words
            val caption = Caption(0, words)
            viewModelScope.launch(Dispatchers.IO) {
                transcriptionRepository.insertCaption(studioParams.transcriptionId, caption)
            }
        }
    }

    fun pauseRecording() {
        _studioUIState.value = StudioUIState.Paused
        audioRecorder.pause()
    }

    fun startPlaying(audioPath: String) {
        _studioUIState.value = StudioUIState.Playing
        audioPlayer.play(audioPath)
    }

    fun seek(time: Int) {
        previousPlaybackState = studioUIState.value
        _studioUIState.value = StudioUIState.Paused
        audioPlayer.pause()
        audioPlayer.seek(time)
    }

    fun pausePlaying() {
        _studioUIState.value = StudioUIState.Paused
        audioPlayer.pause()
    }
}