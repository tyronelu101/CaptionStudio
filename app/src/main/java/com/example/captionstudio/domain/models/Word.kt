package com.example.captionstudio.domain.models

import com.example.captionstudio.database.entities.WordEntity

data class Word(
    val id: Long,
    val captionId: Long,
    val text: String,
    val confidence: Int,
    val startTime: Long,
    val endTime: Long
)

fun Word.toEntity(): WordEntity = WordEntity(
    wordId = this.id,
    captionId = this.captionId,
    text = this.text,
    confidence = this.confidence,
    startTime = startTime,
    endTime = endTime
)

fun WordEntity.toModel(): Word = Word(
    id = this.wordId,
    captionId = this.captionId,
    text = this.text,
    confidence = this.confidence,
    startTime = startTime,
    endTime = endTime
)