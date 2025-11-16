package com.example.captionstudio.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.captionstudio.database.converters.DateConverter
import com.example.captionstudio.database.entities.CaptionEntity
import com.example.captionstudio.database.entities.TranscriptionEntity
import com.example.captionstudio.database.entities.WordEntity


@Database(
    exportSchema = false,
    entities = [TranscriptionEntity::class, CaptionEntity::class, WordEntity::class],
    version = 1
)
@TypeConverters(value = [DateConverter::class])
abstract class CaptionStudioDatabase : RoomDatabase() {
    abstract fun transcriptionDao(): TranscriptionDao
    abstract fun captionsDao(): CaptionsDao
    abstract fun wordDao(): WordDao

}