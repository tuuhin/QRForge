package com.sam.qrforge.feature_generator.data.util

import com.google.zxing.common.BitMatrix
import com.sam.qrforge.feature_generator.domain.models.GeneratedQRModel
import com.sam.qrforge.feature_generator.domain.models.EnclosingRect

fun BitMatrix.toModel(): GeneratedQRModel {
	val enclosingRect = enclosingRectangle.let { array ->
		EnclosingRect(
			left = array[0],
			right = array[0] + array[2],
			top = array[1],
			bottom = array[1] + array[3]
		)
	}
	val array = BooleanArray(width * height)
	for (y in 0..<height) {
		val offset = y * width
		for (x in 0..<width) {
			array[offset + x] = get(x, y)
		}
	}
	return GeneratedQRModel(
		widthInPx = width,
		heightInPx = height,
		enclosingRect = enclosingRect,
		bits = array
	)
}