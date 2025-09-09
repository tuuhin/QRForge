package com.sam.qrforge.domain.enums

enum class ImageMimeTypes {
	JPEG,
	PNG;

	val mimeType: String
		get() = when (this) {
			JPEG -> "image/jpeg"
			PNG -> "image/png"
		}
}