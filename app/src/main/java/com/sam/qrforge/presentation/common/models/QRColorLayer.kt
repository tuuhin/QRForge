package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

@Stable
data class QRColorLayer(val layers: List<QROverlayColor> = emptyList()) {

	fun copyEnsureOneExists(backOff: Color) =
		if (layers.isNotEmpty()) this
		else QRColorLayer(layers = listOf(QROverlayColor(backOff, Offset.Zero)))

	fun copyEnsuresOnlyValidOffsets() =
		copy(layers = layers.filter { (_, offset) -> offset.x in -1f..1f && offset.y in -1f..1f })

	companion object {
		val COLOR_DISCO = QRColorLayer(
			listOf(
				QROverlayColor(Color.Yellow, Offset(.14f, -.14f)),
				QROverlayColor(Color.Red, Offset(.14f, .14f)),
				QROverlayColor(Color.Green, Offset(-.12f, -.14f)),
				QROverlayColor(Color.Blue, Offset(-.12f, .2f)),
			)
		)

		val COLOR_BLOCKS = QRColorLayer(
			listOf(
				QROverlayColor(
					color = Color(color = 0xffe80004),
					offset = Offset(.6f, .6f),
					blendMode = BlendMode.Difference
				),
				QROverlayColor(
					color = Color.Black,
					offset = Offset(.4f, .4f),
					blendMode = BlendMode.Difference
				),
				QROverlayColor(
					color = Color(color = 0xffca70cf),
					offset = Offset.Zero,
					blendMode = BlendMode.Difference
				),
				QROverlayColor(
					color = Color.White,
					offset = Offset(-.2f, -.2f),
					blendMode = BlendMode.Difference
				),
			)
		)
	}
}