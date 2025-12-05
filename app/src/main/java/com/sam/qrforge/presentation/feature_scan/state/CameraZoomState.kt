package com.sam.qrforge.presentation.feature_scan.state

import androidx.compose.runtime.Stable

@Stable
data class CameraZoomState(
	val zoomRatio: () -> Float = { 1f },
	val minZoomRatio: Float = 1f,
	val maxZoomRatio: Float = 1f
) {
	val zoomRange: ClosedFloatingPointRange<Float>
		get() = minZoomRatio..maxZoomRatio
}