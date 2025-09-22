package com.sam.qrforge.presentation.feature_export.state

import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes

data class ExportQRScreenState(
	val verificationState: VerificationState = VerificationState.NOT_VERIFIED,
	val canExport: Boolean = true,
	val selectedMimeType: ImageMimeTypes = ImageMimeTypes.PNG,
	val exportDimensions: ExportDimensions = ExportDimensions.Medium,
) {

	val canVerify: Boolean
		get() = verificationState == VerificationState.NOT_VERIFIED || verificationState == VerificationState.FAILED
}