package com.sam.qrforge.feature_generator.domain.models.qr

import com.sam.qrforge.feature_generator.domain.enums.QRDataFormat

sealed class QRDataModel(val type: QRDataFormat) {

	abstract fun toQRString(): String
}