package com.sam.qrforge.data.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ProvidedTypeConverter
@OptIn(ExperimentalTime::class)
class LocalDateTimeConvertor {

	private val timeZone = TimeZone.currentSystemDefault()

	@TypeConverter
	fun fromLocalTime(time: LocalDateTime): Long = time.toInstant(timeZone).toEpochMilliseconds()

	@TypeConverter
	fun toLocalTime(timeInMillis: Long) =
		Instant.fromEpochMilliseconds(timeInMillis).toLocalDateTime(timeZone)
}