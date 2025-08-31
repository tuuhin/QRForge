package com.sam.qrforge.presentation.common.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val LocalDateTime.Formats.PLAIN_DATE: DateTimeFormat<LocalDateTime>
	get() = LocalDateTime.Format {
		day(padding = Padding.ZERO)
		char(' ')
		monthName(MonthNames.ENGLISH_ABBREVIATED)
		char(' ')
		year()
	}

val LocalDateTime.Formats.PLAIN_DATE_TIME: DateTimeFormat<LocalDateTime>
	get() = LocalDateTime.Format {
		day(padding = Padding.ZERO)
		char(' ')
		monthName(MonthNames.ENGLISH_ABBREVIATED)
		char(' ')
		year()
		char(' ')
		hour()
		char(':')
		minute()
	}