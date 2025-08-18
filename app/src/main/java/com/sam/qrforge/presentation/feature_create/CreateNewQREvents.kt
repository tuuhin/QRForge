package com.sam.qrforge.presentation.feature_create

import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel

sealed interface CreateNewQREvents {

	data class OnUpdateQRContent(val content: QRContentModel) : CreateNewQREvents
	data class OnQRDataTypeChange(val type: QRDataType) : CreateNewQREvents

	data object OnGenerateQR : CreateNewQREvents
	data object CheckLastKnownLocation : CreateNewQREvents
	data class CheckContactsDetails(val uri: String) : CreateNewQREvents
}