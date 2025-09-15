package com.sam.qrforge.data.utils

import android.media.Image

fun Image.toNV21(): ByteArray {
	val yBuffer = planes[0].buffer // Y
	val uBuffer = planes[1].buffer // U
	val vBuffer = planes[2].buffer // V

	val ySize = yBuffer.remaining()
	val uSize = uBuffer.remaining()
	val vSize = vBuffer.remaining()

	val nv21 = ByteArray(ySize + uSize + vSize)

	// Copy Y
	yBuffer.get(nv21, 0, ySize)

	// NV21 format is Y + VU (interleaved)
	// U and V are swapped compared to NV21, so we need to swap them
	var index = ySize
	while (vBuffer.hasRemaining() && uBuffer.hasRemaining()) {
		nv21[index++] = vBuffer.get()
		nv21[index++] = uBuffer.get()
	}

	return nv21
}