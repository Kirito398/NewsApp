package com.example.news.database.utils

import androidx.room.TypeConverter
import java.text.DateFormat
import java.util.Date

class DateTimeTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { DateFormat.getDateTimeInstance().parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.time?.let { DateFormat.getDateTimeInstance().format(it) }
    }
}
