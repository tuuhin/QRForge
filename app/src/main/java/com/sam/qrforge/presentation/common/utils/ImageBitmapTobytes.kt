package com.sam.qrforge.presentation.common.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun ImageBitmap.toBytes(): ByteArray {
	return withContext(Dispatchers.IO) {
		val bitmap = asAndroidBitmap()
		ByteArrayOutputStream().use { stream ->
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
			stream.toByteArray()
		}
	}
}