package com.example.captionstudio.domain.export

interface CaptionExporter {
    suspend fun export(transcriptionId: Long, uri: String)
}