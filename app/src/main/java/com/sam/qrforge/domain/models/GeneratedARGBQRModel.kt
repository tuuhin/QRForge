package com.sam.qrforge.domain.models

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
}
