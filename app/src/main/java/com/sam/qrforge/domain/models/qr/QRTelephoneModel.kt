package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRTelephoneModel(
	val number: String? = null,
) : QRContentModel(type = QRDataType.TYPE_PHONE) {

	private val pattern = Regex("^\\+(?:\\d{1,3})?\\s?(?:\\d{5}\\s\\d{5}|\\d{10})$|^\\d{10}$")

	override val isValid: Boolean
		get() = number?.isNotEmpty() == true && pattern.matches(number)

	override fun toQRString(): String = "tel:$number"
}
