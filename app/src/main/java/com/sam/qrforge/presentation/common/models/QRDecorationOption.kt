package com.sam.qrforge.presentation.common.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class QRDecorationOption(val templateType: QRTemplateOption) {

	abstract val roundness: Float
	abstract val bitsSizeMultiplier: Float
	abstract val contentMargin: Dp
	abstract val backGroundColor: Color?
	abstract val bitsColor: Color?
	abstract val findersColor: Color?
	abstract val isDiamond: Boolean

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
		val frameColor: Color? = null,
	) : QRDecorationOption(templateType = QRTemplateOption.BASIC)

	@Immutable
	data class QRDecorationOptionMinimal(
		override val roundness: Float = 0f,
		override val bitsSizeMultiplier: Float = 1f,
		override val contentMargin: Dp = 0.dp,
		override val isDiamond: Boolean = false,
		override val bitsColor: Color? = null,
		override val findersColor: Color? = null,
		val background: Color? = null,
		val showBackground: Boolean = false,
	) : QRDecorationOption(templateType = QRTemplateOption.MINIMALISTIC) {

		override val backGroundColor: Color?
			get() = if (showBackground) background else null
	}

	@Immutable
	data class QRDecorationOptionColorLayer(
		override val roundness: Float = 0f,
		override val bitsSizeMultiplier: Float = 1f,
		override val contentMargin: Dp = 0.dp,
		override val backGroundColor: Color? = null,
		val coloredLayers: QRColorLayer = QRColorLayer.PowerRangers,
	) : QRDecorationOption(templateType = QRTemplateOption.COLOR_LAYERED) {

		override val bitsColor: Color? get() = null
		override val findersColor: Color? get() = null
		override val isDiamond: Boolean get() = false
	}

	val canHaveBackgroundOption: Boolean
		get() = when (this) {
			is QRDecorationOptionBasic -> true
			is QRDecorationOptionColorLayer -> true
			is QRDecorationOptionMinimal -> showBackground
		}

	fun copyBackgroundColor(color: Color?): QRDecorationOption {
		return when (this) {
			is QRDecorationOptionBasic -> copy(backGroundColor = color)
			is QRDecorationOptionColorLayer -> copy(backGroundColor = color)
			is QRDecorationOptionMinimal -> copy(background = if (showBackground) color else null)
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
				background = backGroundColor,
				isDiamond = isDiamond,
				findersColor = findersColor,
				bitsColor = bitsColor,
			)

			QRTemplateOption.COLOR_LAYERED -> QRDecorationOptionColorLayer(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
			)
		}
	}
}