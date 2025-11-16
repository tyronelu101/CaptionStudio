package com.example.captionstudio.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface BaseDao<T> {

    @Insert
    fun insert(entity: T): Long

    @Update
    fun update(entity: T)

    @Delete
    fun delete(entity: T)

    @Upsert
    fun upsert(entity: T): Long

    @Upsert
    fun upsertAll(entity: List<T>): List<Long>

}