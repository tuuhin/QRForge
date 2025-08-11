package com.sam.qrforge.feature_generator.domain.models.qr

import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat

data class QRGeoPointModel(
	val lat: Double,
	val long: Double
) : QRDataModel(type = QRDataFormat.TYPE_GEO) {

	override fun toQRString(): String = "geo:$lat,$long"
}
