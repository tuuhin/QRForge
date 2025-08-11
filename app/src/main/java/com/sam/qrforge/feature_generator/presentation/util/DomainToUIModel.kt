package com.sam.qrforge.feature_generator.presentation.util

import androidx.compose.ui.geometry.Rect
import com.sam.qrforge.feature_generator.domain.models.EnclosingRect
import com.sam.qrforge.feature_generator.domain.models.GeneratedQRModel
import com.sam.qrforge.feature_generator.presentation.models.GeneratedQRUIModel
import com.sam.qrforge.feature_generator.presentation.models.QRMatrixUIModel

fun EnclosingRect.toComposeRect(): Rect = with(this) {
	Rect(
		left = left.toFloat(),
		right = right.toFloat(),
		top = top.toFloat(),
		bottom = bottom.toFloat()
	)
}

fun GeneratedQRModel.toUIModel(): GeneratedQRUIModel {

	val matrix = buildList {
		for (y in 0..<heightInPx) {
			val offset = y * widthInPx
			val row = buildList {
				for (x in 0..<widthInPx) add(bits[offset + x])
			}
			add(row)
		}
	}

	return GeneratedQRUIModel(
		widthInPx = widthInPx,
		heightInPx = heightInPx,
		enclosingRect = enclosingRect.toComposeRect(),
		matrix = QRMatrixUIModel(matrix)
	)
}