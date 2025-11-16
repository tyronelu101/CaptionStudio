package com.example.captionstudio.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = CaptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("captionId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val wordId: Long = 0L,
    val captionId: Long,
    val text: String,
    val confidence: Int,
    val startTime: Long,
    val endTime: Long
)