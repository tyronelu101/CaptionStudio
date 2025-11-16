package com.example.captionstudio.database.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun fromTimestamp(date: Date): Long = date.time
}