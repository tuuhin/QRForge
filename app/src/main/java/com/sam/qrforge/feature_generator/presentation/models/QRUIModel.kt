package com.sam.qrforge.feature_generator.presentation.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntRect

@Stable
data class QRUIModel(
	val widthInBlocks: Int,
	val heightInBlocks: Int,
	val margin: Int,
	val qrMatrix: QRMatrixUIModel,
) {
	constructor(
		dimension: Int,
		margin: Int,
		boolArray: BooleanArray
	) : this(
		widthInBlocks = dimension,
		heightInBlocks = dimension,
		margin = margin,
		qrMatrix = QRMatrixUIModel(
			matrix = buildList {
				for (y in 0..<dimension) {
					val row = buildList {
						for (x in 0..<dimension) add(boolArray[y * dimension + x])
					}
					add(row)
				}
			},
		)
	)

	val version: Int
		get() = ((widthInBlocks - 2 * margin - 17) / 4).coerceIn(1..40)

	val enclosingRect: IntRect
		get() = IntRect(
			left = margin,
			top = margin,
			right = widthInBlocks - margin,
			bottom = heightInBlocks - margin
		)
}