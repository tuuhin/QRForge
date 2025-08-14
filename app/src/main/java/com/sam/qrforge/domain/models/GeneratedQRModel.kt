package com.sam.qrforge.domain.models

data class GeneratedQRModel(
	val widthInPx: Int,
	val heightInPx: Int,
	val bits: BooleanArray,
	val marginBlocks: Int = 0,
	val qrVersion: Int = 0,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GeneratedQRModel

		if (widthInPx != other.widthInPx) return false
		if (heightInPx != other.heightInPx) return false
		if (!bits.contentEquals(other.bits)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = widthInPx
		result = 31 * result + heightInPx
		result = 31 * result + bits.contentHashCode()
		return result
	}
}
