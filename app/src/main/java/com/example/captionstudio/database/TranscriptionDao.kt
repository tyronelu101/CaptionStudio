package com.example.captionstudio.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.captionstudio.database.entities.TranscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao: BaseDao<TranscriptionEntity> {
    @Query("SELECT * FROM transcriptions where id = :id")
    fun get(id: Long): TranscriptionEntity

    @Query("SELECT * FROM transcriptions")
    fun getTranscriptions(): Flow<List<TranscriptionEntity>>


}