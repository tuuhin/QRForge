package com.sam.qrforge.presentation.feature_export.state

import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes

data class ExportQRScreenState(
	val isExporting: Boolean = false,
	val showTooMuchEdit: Boolean = false,
	val showError: Boolean = false,
	val selectedMimeType: ImageMimeTypes = ImageMimeTypes.PNG,
	val exportDimensions: ExportDimensions = ExportDimensions.Medium,
)