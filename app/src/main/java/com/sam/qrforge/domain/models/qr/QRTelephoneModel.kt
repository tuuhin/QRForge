package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType


data class QRTelephoneModel(
	val number: String? = null,
) : QRContentModel(type = QRDataType.TYPE_PHONE) {

	override val isValid: Boolean
		get() = number?.isNotEmpty() == true && telephonePattern.matches(number)

	override fun toQRString(): String = "tel:$number"

	companion object {
		fun toQRModel(content: String): QRTelephoneModel? {
			if (!content.startsWith("tel:", ignoreCase = true)) return null

			val number = content.removePrefix("tel:").takeIf { it.isNotBlank() }
			return QRTelephoneModel(number = number)
		}
	}
}
