package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

data class QRTelephoneModel(
	val number: String
) : QRContentModel(type = QRDataType.TYPE_PHONE) {

	override fun toQRString(): String = "tel:$number"
}
