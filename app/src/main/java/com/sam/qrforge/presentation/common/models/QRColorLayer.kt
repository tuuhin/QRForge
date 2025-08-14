package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Stable
data class QRColorLayer(val layers: List<QROverlayColor> = emptyList()) {

	fun copyEnsureOneExists(backOff: Color) =
		if (layers.isNotEmpty()) this
		else QRColorLayer(layers = listOf(QROverlayColor(backOff, Offset.Zero)))

	fun copyEnsuresOnlyValidOffsets() =
		copy(layers = layers.filter { (_, offset) -> offset.x in -1f..1f && offset.y in -1f..1f })
}