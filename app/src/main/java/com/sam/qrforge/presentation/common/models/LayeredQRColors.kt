package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed class LayeredQRColors(val name: String) {

	abstract val layers: List<QROverlayColor>

	fun copyEnsureOneExists(backOff: Color): LayeredQRColors {
		return if (layers.isNotEmpty()) this
		else Custom(layers = persistentListOf(QROverlayColor(backOff, Offset.Zero)))
	}

	val filterValidOverlayColor: List<QROverlayColor>
		get() = layers.filter { (_, offset) -> offset.x in -1f..1f && offset.y in -1f..1f }

	data object PowerRangers : LayeredQRColors(name = "POWER RANGERS") {
		override val layers: List<QROverlayColor>
			get() = listOf(
				QROverlayColor(Color.Red, Offset(.1f, .7f)),
				QROverlayColor(Color.Blue, Offset(-.4f, .1f)),
				QROverlayColor(Color.Yellow, Offset(-.2f, .45f)),
				QROverlayColor(Color.Green, Offset(-.3f, .6f)),
				QROverlayColor(Color.White, Offset.Zero),
			)

	}

	data object Blocks : LayeredQRColors(name = "BLOCKS") {
		override val layers: List<QROverlayColor>
			get() = listOf(
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
					offset = Offset(.2f, .2f),
					blendMode = BlendMode.Difference
				),
				QROverlayColor(
					color = Color.Black,
					offset = Offset.Zero,
					blendMode = BlendMode.Difference
				),
				QROverlayColor(
					color = Color.White,
					offset = Offset(-.2f, -.2f),
					blendMode = BlendMode.Difference
				),
			)
	}


	@Stable
	data class Custom(override val layers: ImmutableList<QROverlayColor>) :
		LayeredQRColors("Custom")

	companion object {

		val DEFAULT_OPTIONS: List<LayeredQRColors>
			get() = listOf(PowerRangers, Blocks)
	}
}