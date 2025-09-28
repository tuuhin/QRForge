package com.sam.qrforge.presentation.feature_create.state

import androidx.compose.ui.graphics.ImageBitmap
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.models.qr.QRContentModel

sealed interface CreateQREvents {

	data class OnUpdateQRContent(val content: QRContentModel) : CreateQREvents
	data class OnQRDataTypeChange(val type: QRDataType) : CreateQREvents

	data object CheckLastKnownLocation : CreateQREvents
	data class CheckContactsDetails(val uri: String) : CreateQREvents

	data class ShareGeneratedQR(val bitmap: ImageBitmap) : CreateQREvents
	data object OnPreviewQR : CreateQREvents
}