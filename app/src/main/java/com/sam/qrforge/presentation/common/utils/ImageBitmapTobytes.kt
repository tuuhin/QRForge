package com.sam.qrforge.presentation.common.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.sam.qrforge.domain.enums.ImageMimeTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun ImageBitmap.toBytes(
	format: ImageMimeTypes = ImageMimeTypes.PNG,
	quality: Int = 100
): ByteArray {
	return withContext(Dispatchers.IO) {
		val bitmap = asAndroidBitmap()
		ByteArrayOutputStream().use { stream ->
			val compressFormat = when (format) {
				ImageMimeTypes.JPEG -> Bitmap.CompressFormat.JPEG
				ImageMimeTypes.PNG -> Bitmap.CompressFormat.PNG
			}
			bitmap.compress(compressFormat, quality, stream)
			stream.toByteArray()
		}
	}
}

suspend fun ImageBitmap.toGrayScaleBytes(): ByteArray {
	val width = this.width
	val height = this.height
	val grayBytes = ByteArray(width * height)
	withContext(Dispatchers.Default) {
		val pixels = IntArray(width * height)
		asAndroidBitmap().getPixels(pixels, 0, width, 0, 0, width, height)
		var index = 0
		repeat(height) {
			repeat(width) {
				val pixel = pixels[index]

				val r = (pixel shr 16) and 0xFF
				val g = (pixel shr 8) and 0xFF
				val b = (pixel) and 0xFF

				// Convert RGB → grayscale luminance
				val gray = ((r * 0.299) + (g * 0.587) + (b * 0.114)).toInt()
				grayBytes[index] = gray.toByte()
				index++
			}
		}
	}
	return grayBytes
}