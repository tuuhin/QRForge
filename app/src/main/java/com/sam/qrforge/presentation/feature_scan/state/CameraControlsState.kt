package com.sam.qrforge.presentation.feature_scan.state

import androidx.camera.core.SurfaceRequest
import androidx.compose.runtime.Stable

@Stable
data class CameraControlsState(
	val surfaceRequest: SurfaceRequest? = null,
	val focusState: CameraFocusState = CameraFocusState.Unspecified,
	val isFlashEnabled: Boolean = false,
	val zoomState: CameraZoomState = CameraZoomState(),
)