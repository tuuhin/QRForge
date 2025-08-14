package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRPlainTextModel(
	val text: String
) : QRContentModel(type = QRDataType.TYPE_TEXT) {

	override fun toQRString(): String = text
}
