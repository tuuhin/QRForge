package com.sam.qrforge.feature_generator.presentation.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect

@Immutable
data class GeneratedQRUIModel(
	val widthInPx: Int,
	val heightInPx: Int,
	val enclosingRect: Rect,
	val matrix: QRMatrixUIModel,
)