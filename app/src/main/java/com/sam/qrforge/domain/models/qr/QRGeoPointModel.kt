package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRGeoPointModel(
	val lat: Double = 0.0,
	val long: Double = 0.0,
) : QRContentModel(type = QRDataType.TYPE_GEO) {

	override val isValid: Boolean = lat in -90.0..90.0 && long in -180.0..180.0

	override fun toQRString(): String = "geo:$lat,$long"
}
