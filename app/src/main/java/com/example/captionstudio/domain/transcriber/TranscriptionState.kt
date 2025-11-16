package com.example.captionstudio.domain.transcriber

import com.example.captionstudio.domain.models.Word

sealed class TranscriberResult {
    class PartialResult(val text: String) : TranscriberResult()
    class FinalResult(
        val words: List<Word> = emptyList(),
    ) : TranscriberResult() {
        val startTime: Long
            get() = words.firstOrNull()?.startTime ?: 0L
        val endTime: Long
            get() = words.lastOrNull()?.endTime ?: 0L
    }
}
