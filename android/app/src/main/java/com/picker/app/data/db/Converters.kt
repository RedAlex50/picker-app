package com.picker.app.data.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

class TimeConverters {
    @TypeConverter fun fromInstant(v: Instant?): Long? = v?.toEpochMilli()
    @TypeConverter fun toInstant(v: Long?): Instant? = v?.let(Instant::ofEpochMilli)
    @TypeConverter fun fromLocalDate(v: LocalDate?): String? = v?.toString()
    @TypeConverter fun toLocalDate(v: String?): LocalDate? = v?.let(LocalDate::parse)
}
