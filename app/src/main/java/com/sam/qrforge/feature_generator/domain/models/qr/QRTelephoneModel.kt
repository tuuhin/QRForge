package com.sam.qrforge.feature_generator.domain.models.qr

import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat

data class QRTelephoneModel(
	val number: String
) : QRDataModel(type = QRDataFormat.TYPE_PHONE) {

	override fun toQRString(): String = "tel:$number"
}
