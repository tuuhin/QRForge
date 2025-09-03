package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

sealed class QRContentModel(val type: QRDataType) {

	protected val telephonePattern =
		Regex("^\\+(?:\\d{1,3})?\\s?(?:\\d{5}\\s\\d{5}|\\d{10})$|^\\d{10}$")

	abstract fun toQRString(): String

	abstract val isValid: Boolean
}