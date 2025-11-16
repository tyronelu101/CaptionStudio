package com.example.captionstudio.data

import com.example.captionstudio.domain.models.Caption
import com.example.captionstudio.domain.models.Transcription
import kotlinx.coroutines.flow.Flow

interface TranscriptionRepository {
    suspend fun insertTranscription(transcription: Transcription): Long
    suspend fun getTranscription(id: Long): Transcription
    suspend fun delete(transcription: Transcription)

    suspend fun insertCaption(transcriptionId: Long, caption: Caption)

    fun getTranscriptions(): Flow<List<Transcription>>
    fun getCaptionsForTranscription(transcriptionId: Long): Flow<List<Caption>>
}
