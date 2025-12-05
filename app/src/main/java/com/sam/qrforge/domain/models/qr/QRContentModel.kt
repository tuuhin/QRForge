package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

sealed class QRContentModel(val type: QRDataType) {

	abstract fun toQRString(): String

	abstract val isValid: Boolean

	companion object {

		@JvmStatic
		protected val telephonePattern: Regex
			get() = Regex("^\\+(?:\\d{1,3})?\\s?(?:\\d{5}\\s\\d{5}|\\d{10})$|^\\d{10}$")
	}
}