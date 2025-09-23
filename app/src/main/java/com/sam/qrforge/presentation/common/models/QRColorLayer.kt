package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed class QRColorLayer(val name: String) {

	abstract val layers: List<QROverlayColor>

	fun copyEnsureOneExists(backOff: Color) =
		if (layers.isNotEmpty()) this
		else Custom(layers = persistentListOf(QROverlayColor(backOff, Offset.Zero)))

	val filterValidOverlayColor: List<QROverlayColor>
		get() = layers.filter { (_, offset) -> offset.x in -1f..1f && offset.y in -1f..1f }

	data object PowerRangers : QRColorLayer(name = "POWER RANGERS") {
		override val layers: List<QROverlayColor>
			get() = listOf(
				QROverlayColor(Color.Red, Offset(.14f, .14f)),
				QROverlayColor(Color.Blue, Offset(-.12f, .2f)),
				QROverlayColor(Color.Yellow, Offset(-.2f, .1f)),
				QROverlayColor(Color.Green, Offset(-.16f, .1f)),
				QROverlayColor(Color.White, Offset.Zero),
			)
	}

	data object Blocks : QRColorLayer(name = "BLOCKS") {
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
	data class Custom(override val layers: ImmutableList<QROverlayColor>) : QRColorLayer("Custom")

	companion object {

		val DEFAULT_OPTIONS: List<QRColorLayer>
			get() = listOf(PowerRangers, Blocks)
	}
}