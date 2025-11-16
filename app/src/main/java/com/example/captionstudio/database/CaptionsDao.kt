package com.example.captionstudio.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.captionstudio.database.entities.CaptionEntity
import com.example.captionstudio.database.entities.CaptionWithWords
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptionsDao: BaseDao<CaptionEntity> {

    @Transaction
    @Query("SELECT * FROM captions where transcriptionId = :transcriptionId")
    fun getCaptionsWithWordsByTranscriptionId(transcriptionId: Long): Flow<List<CaptionWithWords>>
}