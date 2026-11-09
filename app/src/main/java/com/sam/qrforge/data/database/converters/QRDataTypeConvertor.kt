package com.sam.qrforge.data.database.converters

import androidx.room3.ColumnTypeConverter
import androidx.room3.ProvidedColumnTypeConverter
import com.sam.qrforge.domain.enums.QRDataType

@ProvidedColumnTypeConverter
class QRDataTypeConvertor {

	@ColumnTypeConverter
	fun fromDataType(type: QRDataType) = type.name

	@ColumnTypeConverter
	fun toDataType(name: String): QRDataType {
		return try {
			QRDataType.valueOf(name)
		} catch (_: IllegalArgumentException) {
			QRDataType.TYPE_TEXT
		}
	}
}