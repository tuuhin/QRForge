package com.sam.qrforge.data.mappers

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.domain.models.GeneratedARGBQRModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun ImageBitmap.toRGBAModel(): GeneratedARGBQRModel {
	return withContext(Dispatchers.IO) {
		val pixels = IntArray(width * height)
		readPixels(pixels)

		GeneratedARGBQRModel(pixels = pixels, width = width, height)
	}
}

suspend fun ImageBitmap.toCompressedByteArray(
	compressFormat: ImageMimeTypes = ImageMimeTypes.PNG,
	quality: Int = 100
): ByteArray {
	return withContext(Dispatchers.IO) {
		val bitmap = asAndroidBitmap()
		ByteArrayOutputStream().use { stream ->
			val format = when (compressFormat) {
				ImageMimeTypes.JPEG -> Bitmap.CompressFormat.JPEG
				ImageMimeTypes.PNG -> Bitmap.CompressFormat.PNG
			}
			bitmap.compress(format, quality, stream)
			stream.toByteArray()
		}
	}
}