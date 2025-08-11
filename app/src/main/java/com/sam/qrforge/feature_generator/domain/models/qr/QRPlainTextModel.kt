package com.sam.qrforge.feature_generator.domain.models.qr

import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat

data class QRPlainTextModel(
	val text: String
) : QRDataModel(type = QRDataFormat.TYPE_TEXT) {

	override fun toQRString(): String = text
}
