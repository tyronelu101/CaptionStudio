package com.example.captionstudio.domain.models

import com.example.captionstudio.database.entities.TranscriptionEntity
import java.util.Date

data class Transcription(
    val id: Long = 0L, val name: String, val language: String, val audioURI: String, val date: Date
)

fun TranscriptionEntity.toModel(): Transcription = Transcription(
    id = this.id,
    name = this.name,
    language = this.language,
    audioURI = this.audioURI,
    date = this.date
)

fun Transcription.toEntity(): TranscriptionEntity = TranscriptionEntity(
    id = this.id,
    name = this.name,
    language = this.language,
    audioURI = this.audioURI,
    date = this.date
)