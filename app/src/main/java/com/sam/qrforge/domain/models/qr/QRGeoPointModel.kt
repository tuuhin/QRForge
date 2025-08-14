package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRGeoPointModel(
	val lat: Double,
	val long: Double
) : QRContentModel(type = QRDataType.TYPE_GEO) {

	override fun toQRString(): String = "geo:$lat,$long"
}
