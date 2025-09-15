package com.sam.qrforge.presentation.feature_scan.state

import androidx.compose.ui.geometry.Offset

sealed interface CameraControllerEvents {
	data object BindCamera : CameraControllerEvents
	data object UnBindCamera : CameraControllerEvents

	data object ToggleFlash : CameraControllerEvents
	data class OnChangeCaptureMode(val type: CaptureType) : CameraControllerEvents
	data object CaptureImage : CameraControllerEvents

	data class TapToFocus(val offset: Offset) : CameraControllerEvents

	data class OnZoomLevelChange(val zoom: Float, val isRelative: Boolean = false) :
		CameraControllerEvents

	data class OnSelectImageURI(val uri: String) : CameraControllerEvents
	data object OnClearAnalysisResult : CameraControllerEvents
}