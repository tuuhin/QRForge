package com.sam.qrforge.feature_generator.domain.models

data class GeneratedQRModel(
	val widthInPx: Int,
	val heightInPx: Int,
	val enclosingRect: EnclosingRect,
	val bits: BooleanArray,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GeneratedQRModel

		if (widthInPx != other.widthInPx) return false
		if (heightInPx != other.heightInPx) return false
		if (enclosingRect != other.enclosingRect) return false
		if (!bits.contentEquals(other.bits)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = widthInPx
		result = 31 * result + heightInPx
		result = 31 * result + enclosingRect.hashCode()
		result = 31 * result + bits.contentHashCode()
		return result
	}
}
