package com.sam.qrforge.feature_generator.domain.models

sealed class ExportDimensions(val widthInPx: Int, val heightInPx: Int) {
	data object ExtraSmall : ExportDimensions(150, 150)
	data object Small : ExportDimensions(250, 250)
	data object Medium : ExportDimensions(400, 400)
	data object Large : ExportDimensions(600, 600)
	data object ExtraLarge : ExportDimensions(1000, 1000)
	data class Custom(val w: Int, val h: Int) : ExportDimensions(w, h)
}
