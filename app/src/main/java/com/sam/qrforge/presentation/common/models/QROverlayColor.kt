package com.sam.qrforge.presentation.common.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

data class QROverlayColor(
	val color: Color,
	val offset: Offset = Offset.Zero,
	val blendMode: BlendMode? = null
)
