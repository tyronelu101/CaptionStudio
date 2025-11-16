package com.example.captionstudio.domain.models

import com.example.captionstudio.database.entities.CaptionWithWords

data class Caption(
    val id: Long,
    val words: List<Word>,
) {
    val startingTime: Long
        get() = if (words.isEmpty()) 0L else words.first().startTime
    val endingTime: Long
        get() = if (words.isEmpty()) 0L else words.last().endTime
}

fun CaptionWithWords.toModel(): Caption = Caption(
    id = this.caption.id,
    words = this.words.map {
        it.toModel()
    },
)