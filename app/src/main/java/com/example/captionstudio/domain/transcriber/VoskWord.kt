package com.example.captionstudio.domain.transcriber

data class VoskWord(
    val conf: Double,
    val start: Double,
    val end: Double,
    val word: String
)