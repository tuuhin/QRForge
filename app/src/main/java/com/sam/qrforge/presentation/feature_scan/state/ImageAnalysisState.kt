package com.sam.qrforge.presentation.feature_scan.state

data class ImageAnalysisState(
	val isAnalysing: Boolean = false,
	val captureType: CaptureType = CaptureType.AUTO,
	val isAnalyserSet: Boolean = false,
) {
	val isAnalyzerRunning: Boolean
		get() = isAnalyserSet && captureType == CaptureType.AUTO
}