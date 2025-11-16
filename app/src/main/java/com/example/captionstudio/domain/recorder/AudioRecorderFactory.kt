package com.example.captionstudio.domain.recorder

interface AudioRecorderFactory {
    fun create(audioPath: String): AudioRecorder
}