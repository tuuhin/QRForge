package com.sam.qrforge.domain.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GeneratedARGBQRModel(
	val pixels: IntArray,
	val width: Int,
	val height: Int
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GeneratedARGBQRModel

		if (width != other.width) return false
		if (height != other.height) return false
		if (!pixels.contentEquals(other.pixels)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = width
		result = 31 * result + height
		result = 31 * result + pixels.contentHashCode()
		return result
	}

	suspend fun toGrayIfTransparent(): GeneratedARGBQRModel {
		return withContext(Dispatchers.IO) {
			var hasTransparent = false
			val newPixels = IntArray(pixels.size) { index ->
				val color = pixels[index]
				val alpha = (color shr 24) and 0xFF
				if (alpha < 255) {
					hasTransparent = true
					0xFF888888.toInt() // solid gray
				} else color
			}
			if (!hasTransparent) this@GeneratedARGBQRModel
			else GeneratedARGBQRModel(pixels = newPixels, width = width, height = height)
		}
	}
}
