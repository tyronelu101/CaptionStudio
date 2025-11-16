package com.example.captionstudio.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "captions",
    foreignKeys = [
        ForeignKey(
            entity = TranscriptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("transcriptionId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CaptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val transcriptionId: Long
)

data class CaptionWithWords(
    @Embedded
    val caption: CaptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "captionId"
    )
    val words: List<WordEntity>
)