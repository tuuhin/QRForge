package com.sam.qrforge.data.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.sam.qrforge.domain.enums.QRDataType

@ProvidedTypeConverter
class QRDataTypeConvertor {

	@TypeConverter
	fun fromDataType(type: QRDataType) = type.name

	@TypeConverter
	fun toDataType(name: String): QRDataType {
		return try {
			QRDataType.valueOf(name)
		} catch (_: IllegalArgumentException) {
			QRDataType.TYPE_TEXT
		}
	}
}