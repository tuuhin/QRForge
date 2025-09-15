package com.sam.qrforge.presentation.feature_scan.state

import android.graphics.Bitmap
import androidx.compose.runtime.Stable

@Stable
data class CameraCaptureState(
	val isCapturing: Boolean = false,
	val captureProgress: () -> Float = { 0f },
	val postCapturePreview: Bitmap? = null,
	val canPropagateProgress: Boolean = false,
)
