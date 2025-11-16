package com.example.captionstudio.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captionstudio.data.TranscriptionRepository
import com.example.captionstudio.domain.models.Transcription
import com.example.captionstudio.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val transcriptionRepository: TranscriptionRepository) :
    ViewModel() {

    private val _saveResult: MutableStateFlow<Result<Transcription>?> =
        MutableStateFlow(
            null
        )
    val saveResult: StateFlow<Result<Transcription>?> = _saveResult

    val transcriptions: StateFlow<List<Transcription>> =
        transcriptionRepository.getTranscriptions().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun insertNewTranscription(transcription: Transcription) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = transcriptionRepository.insertTranscription(transcription)
            val transcription = transcription.copy(id = id)
            _saveResult.value = Result.Success(transcription)
        }
    }

    fun deleteTranscription(transcription: Transcription) {
        viewModelScope.launch(Dispatchers.IO) {
            transcriptionRepository.delete(transcription)
        }
    }

    fun reset() {
        _saveResult.value = null
    }
}