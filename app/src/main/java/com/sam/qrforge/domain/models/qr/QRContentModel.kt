package com.sam.qrforge.domain.models.qr

import com.sam.qrforge.domain.enums.QRDataType

sealed class QRContentModel(val type: QRDataType) {

	abstract fun toQRString(): String

	abstract val isValid: Boolean
}