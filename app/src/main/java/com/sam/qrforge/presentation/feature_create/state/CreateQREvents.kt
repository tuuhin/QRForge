package com.sam.qrforge.presentation.feature_create.state

import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel

sealed interface CreateQREvents {

	data class OnUpdateQRContent(val content: QRContentModel) : CreateQREvents
	data class OnQRDataTypeChange(val type: QRDataType) : CreateQREvents

	data object CheckLastKnownLocation : CreateQREvents
	data class CheckContactsDetails(val uri: String) : CreateQREvents

}