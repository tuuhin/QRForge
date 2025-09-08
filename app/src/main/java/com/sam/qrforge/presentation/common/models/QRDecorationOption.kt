package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
sealed class QRDecorationOption(
	val templateType: QRTemplateOption,
	open val roundness: Float = 0f,
	open val bitsSizeMultiplier: Float = 1f,
	open val contentMargin: Dp = 0.dp,
	open val backGroundColor: Color? = null,
	open val bitsColor: Color? = null,
	open val findersColor: Color? = null,
	open val isDiamond: Boolean = false,
) {

	@Immutable
	data class QRDecorationOptionBasic(
		override val roundness: Float = 0f,
		override val bitsSizeMultiplier: Float = 1f,
		override val contentMargin: Dp = 0.dp,
		override val backGroundColor: Color? = null,
		override val isDiamond: Boolean = false,
		override val bitsColor: Color? = null,
		override val findersColor: Color? = null,
		val showFrame: Boolean = false,
		val alignmentColor: Color? = null,
		val timingColor: Color? = null,
		val frameColor: Color? = null,
	) : QRDecorationOption(
		templateType = QRTemplateOption.BASIC,
		roundness = roundness,
		contentMargin = contentMargin,
		bitsSizeMultiplier = bitsSizeMultiplier,
		backGroundColor = backGroundColor,
		isDiamond = isDiamond,
		bitsColor = bitsColor,
		findersColor = findersColor,
	)

	@Immutable
	data class QRDecorationOptionMinimal(
		override val roundness: Float = 0f,
		override val bitsSizeMultiplier: Float = 1f,
		override val contentMargin: Dp = 0.dp,
		override val backGroundColor: Color? = null,
		override val isDiamond: Boolean = false,
		override val bitsColor: Color? = null,
		override val findersColor: Color? = null,
		val showBackground: Boolean = false,
	) : QRDecorationOption(
		templateType = QRTemplateOption.MINIMALISTIC,
		roundness = roundness,
		contentMargin = contentMargin,
		bitsSizeMultiplier = bitsSizeMultiplier,
		backGroundColor = backGroundColor,
		isDiamond = isDiamond,
		bitsColor = bitsColor,
		findersColor = findersColor,
	)

	@Immutable
	data class QRDecorationOptionColorLayer(
		override val roundness: Float = 0f,
		override val bitsSizeMultiplier: Float = 1f,
		override val contentMargin: Dp = 0.dp,
		override val backGroundColor: Color? = null,
		override val isDiamond: Boolean = false,
		val coloredLayers: QRColorLayer = QRColorLayer.COLOR_DISCO,
	) : QRDecorationOption(
		templateType = QRTemplateOption.COLOR_LAYERED,
		roundness = roundness,
		contentMargin = contentMargin,
		bitsSizeMultiplier = bitsSizeMultiplier,
		backGroundColor = backGroundColor,
		isDiamond = isDiamond,
	)

	fun copyBackgroundColor(color: Color?): QRDecorationOption {
		return when (this) {
			is QRDecorationOptionBasic -> copy(backGroundColor = color)
			is QRDecorationOptionColorLayer -> copy(backGroundColor = color)
			is QRDecorationOptionMinimal -> copy(backGroundColor = color)
		}
	}

	fun copyBitsColor(color: Color?): QRDecorationOption {
		return when (this) {
			is QRDecorationOptionBasic -> copy(bitsColor = color)
			is QRDecorationOptionMinimal -> copy(bitsColor = color)
			is QRDecorationOptionColorLayer -> this
		}
	}

	fun copyFinderColor(color: Color?): QRDecorationOption {
		return when (this) {
			is QRDecorationOptionBasic -> copy(findersColor = color)
			is QRDecorationOptionMinimal -> copy(findersColor = color)
			is QRDecorationOptionColorLayer -> this
		}
	}

	fun copyProperties(
		roundness: Float? = null,
		bitsSizeMultiplier: Float? = null,
		contentMargin: Dp? = null,
		isDiamond: Boolean? = null,
	): QRDecorationOption {
		return when (this) {
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
				isDiamond = isDiamond ?: this.isDiamond
			)
		}
	}

	fun swtichTemplate(template: QRTemplateOption): QRDecorationOption {
		return when (template) {
			QRTemplateOption.BASIC -> QRDecorationOptionBasic(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
				findersColor = findersColor,
				bitsColor = bitsColor,
			)

			QRTemplateOption.MINIMALISTIC -> QRDecorationOptionMinimal(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
				findersColor = findersColor,
				bitsColor = bitsColor,
			)

			QRTemplateOption.COLOR_LAYERED -> QRDecorationOptionColorLayer(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
			)
		}
	}
}