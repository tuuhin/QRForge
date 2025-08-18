package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRSmsModel(
	val phoneNumber: String? = null,
	val message: String? = null,
) : QRContentModel(type = QRDataType.TYPE_SMS) {

	private val pattern = Regex("^\\+(?:\\d{1,3})?\\s?(?:\\d{5}\\s\\d{5}|\\d{10})$|^\\d{10}$")

	override val isValid: Boolean
		get() {
			val isNumberGood = phoneNumber?.isNotEmpty() == true && pattern.matches(phoneNumber)
			val isMessageGood = message?.isNotEmpty() == true

			return isNumberGood && isMessageGood
		}

	override fun toQRString(): String = buildString {
		append("SMSTO:")
		append(phoneNumber)
		message?.let { append(":$message") }
	}
}