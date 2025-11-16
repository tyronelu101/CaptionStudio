package com.example.captionstudio.database

import androidx.room.Dao
import com.example.captionstudio.database.entities.WordEntity

@Dao
interface WordDao: BaseDao<WordEntity> {

}