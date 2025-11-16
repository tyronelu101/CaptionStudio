package com.example.captionstudio.domain.recorder

import kotlinx.coroutines.flow.Flow

interface AudioRecorder {
    fun start(path: String): Flow<List<Byte>>
    fun pause()
    fun stop()
    fun getDuration(): Long
}