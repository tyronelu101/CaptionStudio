package com.example.captionstudio.domain.transcriber

import kotlinx.coroutines.flow.MutableSharedFlow

interface Transcriber {
    val resultFlow: MutableSharedFlow<TranscriberResult>

    suspend fun transcribe(data: ByteArray)
    fun stop()
}