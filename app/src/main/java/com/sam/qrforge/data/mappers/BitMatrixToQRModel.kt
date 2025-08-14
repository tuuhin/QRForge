package com.sam.qrforge.data.mappers

import com.google.zxing.common.BitMatrix
import com.sam.qrforge.domain.models.GeneratedQRModel

fun BitMatrix.toModel(): GeneratedQRModel {
	val array = BooleanArray(width * height)
	for (y in 0..<height) {
		val offset = y * width
		for (x in 0..<width) array[offset + x] = get(x, y)
	}

	val margin = (width - enclosingRectangle[2]) / 2

	return GeneratedQRModel(
		widthInPx = width,
		heightInPx = height,
		marginBlocks = margin,
		bits = array
	)
}