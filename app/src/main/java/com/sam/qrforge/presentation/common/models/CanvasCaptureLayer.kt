package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer

/*
A wrapper around graphics layer to treat [GraphicsLayer] as stable
 */
@Stable
data class CanvasCaptureLayer(val layer: GraphicsLayer? = null) {

	suspend fun captureBitmap() = layer?.toImageBitmap()

	companion object {
		@Composable
		fun rememberCaptureLayer(): CanvasCaptureLayer {
			val graphicsLayer = rememberGraphicsLayer()
			return CanvasCaptureLayer(graphicsLayer)
		}
	}
}
