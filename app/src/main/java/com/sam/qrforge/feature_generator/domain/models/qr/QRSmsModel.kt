package com.sam.qrforge.feature_generator.domain.models.qr

import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat

data class QRSmsModel(
	val phoneNumber: String,
	val message: String? = null,
) : QRDataModel(type = QRDataFormat.TYPE_SMS) {

	override fun toQRString(): String = buildString {
		append("SMSTO:")
		append(phoneNumber)
		message?.let { append(":$message") }
	}
}