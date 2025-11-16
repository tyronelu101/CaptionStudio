package com.example.captionstudio.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transcriptions")
data class TranscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val language: String,
    val audioURI: String,
    val date: Date = Date(System.currentTimeMillis())
)