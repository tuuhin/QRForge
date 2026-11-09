package com.sam.qrforge.data.database.converters

import androidx.room3.ColumnTypeConverter
import androidx.room3.ProvidedColumnTypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@ProvidedColumnTypeConverter
class LocalDateTimeConvertor {

	private val timeZone = TimeZone.currentSystemDefault()

	@ColumnTypeConverter
	fun fromLocalTime(time: LocalDateTime): Long = time.toInstant(timeZone).toEpochMilliseconds()

	@ColumnTypeConverter
	fun toLocalTime(timeInMillis: Long) =
		Instant.fromEpochMilliseconds(timeInMillis).toLocalDateTime(timeZone)
}