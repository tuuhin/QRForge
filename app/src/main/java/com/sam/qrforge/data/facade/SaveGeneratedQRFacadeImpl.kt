package com.sam.qrforge.data.facade

import android.content.Context
import androidx.core.content.FileProvider
import com.sam.qrforge.domain.facade.SaveGeneratedQRFacade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.random.Random

class SaveGeneratedQRFacadeImpl(private val context: Context) : SaveGeneratedQRFacade {

	private val directory by lazy {
		File(context.cacheDir, "qr")
			.apply { mkdirs() }
	}

	override suspend fun prepareFileToShare(bytes: ByteArray): Result<String> {
		return try {
			val fileUri = withContext(Dispatchers.IO) {
				// create new file
				val contentFile = File(directory, "qr_content_${Random.nextInt() / 100}.png")
					.apply { if (!exists()) createNewFile() }
				// copy the content to the new file
				contentFile.outputStream().use { stream -> stream.write(bytes) }
				FileProvider.getUriForFile(context, context.packageName, contentFile)
			}
			Result.success(fileUri.toString())
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}