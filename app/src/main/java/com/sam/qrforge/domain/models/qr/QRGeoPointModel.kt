package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRGeoPointModel(
	val lat: Double = 0.0,
	val long: Double = 0.0,
) : QRContentModel(type = QRDataType.TYPE_GEO) {

	override val isValid: Boolean = lat in -90.0..90.0 && long in -180.0..180.0

	override fun toQRString(): String = "geo:$lat,$long"

	companion object {
		fun toQRModel(content: String): QRGeoPointModel? {
			if (!content.startsWith("geo:", ignoreCase = true)) return null

			val coordinates = content.removePrefix("geo:").split(",", limit = 2)
			if (coordinates.size != 2) return null

			val lat = coordinates.getOrNull(0)?.toDoubleOrNull() ?: 0.0
			val long = coordinates.getOrNull(1)?.toDoubleOrNull() ?: 0.0

			return QRGeoPointModel(lat, long)
		}

	}
}
