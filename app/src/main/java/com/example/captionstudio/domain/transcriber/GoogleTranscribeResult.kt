package com.example.captionstudio.domain.transcriber

sealed class GoogleTranscribeResult {
    data class InterimResult(val transcript: String) : GoogleTranscribeResult()
    data class FinalResult(
        val transcript: List<GoogleWord>,
        val time: String,
    ) : GoogleTranscribeResult()
}

data class GoogleWord(
    val word: String,
    val confidence: Float
)
