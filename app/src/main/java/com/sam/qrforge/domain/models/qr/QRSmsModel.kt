package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRSmsModel(
	val phoneNumber: String,
	val message: String? = null,
) : QRContentModel(type = QRDataType.TYPE_SMS) {

	override fun toQRString(): String = buildString {
		append("SMSTO:")
		append(phoneNumber)
		message?.let { append(":$message") }
	}
}