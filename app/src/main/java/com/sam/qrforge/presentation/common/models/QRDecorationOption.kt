package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
sealed class QRDecorationOption(val templateType: QRTemplateOption) {

	@Stable
	data class QRDecorationOptionBasic(
		val roundness: Float = 0f,
		val bitsSizeMultiplier: Float = 1f,
		val contentMargin: Dp = 0.dp,
		val showFrame: Boolean = false,
		val isDiamond: Boolean = false,
		val bitsColor: Color? = null,
		val alignmentColor: Color? = null,
		val findersColor: Color? = null,
		val backGroundColor: Color? = null,
		val timingColor: Color? = null,
		val frameColor: Color? = null,
	) : QRDecorationOption(QRTemplateOption.BASIC)

	@Stable
	data class QRDecorationOptionMinimal(
		val roundness: Float = 0f,
		val bitsSizeMultiplier: Float = 1f,
		val contentMargin: Dp = 0.dp,
		val isDiamond: Boolean = false,
		val showBackground: Boolean = false,
		val findersColor: Color? = null,
		val bitsColor: Color? = null,
	) : QRDecorationOption(QRTemplateOption.MINIMALISTIC)

	@Stable
	data class QRDecorationOptionColorLayer(
		val coloredLayers: () -> QRColorLayer = { QRColorLayer.COLOR_DISCO },
		val contentMargin: Dp = 0.dp,
		val roundness: Float = 0f,
		val bitsSizeMultiplier: Float = 1f,
		val isDiamond: Boolean = false,
		val backGroundColor: Color? = null,
	) : QRDecorationOption(templateType = QRTemplateOption.COLOR_LAYERED)

}