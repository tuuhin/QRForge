package com.sam.qrforge.presentation.common.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionColorLayer
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionMinimal

fun QRDecorationOption.copyBlockShape(isDiamond: Boolean) = when (this) {
	is QRDecorationOptionBasic -> copy(isDiamond = isDiamond)
	is QRDecorationOptionColorLayer -> this
	is QRDecorationOptionMinimal -> copy(isDiamond = isDiamond)
}

fun QRDecorationOption.copyBackgroundColor(color: Color?) = when (this) {
	is QRDecorationOptionBasic -> copy(backGroundColor = color)
	is QRDecorationOptionColorLayer -> copy(backGroundColor = color)
	is QRDecorationOptionMinimal -> copy(background = if (showBackground) color else null)
}

fun QRDecorationOption.copyBitsColor(color: Color?) = when (this) {
	is QRDecorationOptionBasic -> copy(bitsColor = color)
	is QRDecorationOptionMinimal -> copy(bitsColor = color)
	is QRDecorationOptionColorLayer -> this
}

fun QRDecorationOption.copyFinderColor(color: Color?) = when (this) {
	is QRDecorationOptionBasic -> copy(findersColor = color)
	is QRDecorationOptionMinimal -> copy(findersColor = color)
	is QRDecorationOptionColorLayer -> this
}

fun QRDecorationOption.copyProperties(
	roundness: Float? = null,
	bitsSizeMultiplier: Float? = null,
	contentMargin: Dp? = null,
	isDiamond: Boolean? = null,
) = when (this) {
	is QRDecorationOptionBasic -> copy(
		roundness = roundness ?: this.roundness,
		bitsSizeMultiplier = bitsSizeMultiplier ?: this.bitsSizeMultiplier,
		contentMargin = contentMargin ?: this.contentMargin,
		isDiamond = isDiamond ?: this.isDiamond,
	)

	is QRDecorationOptionMinimal -> copy(
		roundness = roundness ?: this.roundness,
		bitsSizeMultiplier = bitsSizeMultiplier ?: this.bitsSizeMultiplier,
		contentMargin = contentMargin ?: this.contentMargin,
		isDiamond = isDiamond ?: this.isDiamond
	)

	is QRDecorationOptionColorLayer -> copy(
		roundness = roundness ?: this.roundness,
		bitsSizeMultiplier = bitsSizeMultiplier ?: this.bitsSizeMultiplier,
		contentMargin = contentMargin ?: this.contentMargin,
	)
}
