package com.sam.qrforge.presentation.feature_export.state

import androidx.compose.ui.graphics.ImageBitmap
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption

sealed interface ExportQRScreenEvents {
	data class OnDecorationChange(val decoration: QRDecorationOption) : ExportQRScreenEvents
	data class OnQRTemplateChange(val template: QRTemplateOption) : ExportQRScreenEvents
	data class OnExportDimensionChange(val dimen: ExportDimensions) : ExportQRScreenEvents
	data class OnExportBitmap(val bitmap: ImageBitmap) : ExportQRScreenEvents
}