package com.sam.qrforge.data.facade

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.exception.CannotCreateFileException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.random.Random

class FileStorageFacadeImpl(private val context: Context) : FileStorageFacade {

	private val directory by lazy {
		File(context.cacheDir, "qr").apply { mkdirs() }
	}

	private val imagesParentURI: Uri
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
			MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
		else MediaStore.Images.Media.EXTERNAL_CONTENT_URI


	override suspend fun saveContentToShare(bytes: ByteArray): Result<String> {
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

	override suspend fun saveImageContentToStorage(bytes: ByteArray): Result<String> {
		val contentValues = ContentValues().apply {
			put(
				MediaStore.Audio.AudioColumns.RELATIVE_PATH,
				Environment.DIRECTORY_PICTURES + File.separator + "QRForge"
			)
			put(MediaStore.Audio.AudioColumns.DISPLAY_NAME, "exported_${Random.nextInt() / 100}")
			put(MediaStore.Audio.AudioColumns.MIME_TYPE, "images/png")
			put(MediaStore.Audio.AudioColumns.DATE_ADDED, System.currentTimeMillis())
			put(MediaStore.Audio.AudioColumns.IS_PENDING, 1)
		}

		val updatedMetaData = ContentValues().apply {
			put(MediaStore.Audio.AudioColumns.IS_PENDING, 0)
			put(MediaStore.Audio.AudioColumns.DATE_MODIFIED, System.currentTimeMillis())
		}

		// creates the new uri if failed return null
		val newURI = withContext(Dispatchers.IO) {
			context.contentResolver.insert(imagesParentURI, contentValues)
		} ?: return Result.failure(CannotCreateFileException())

		return try {
			withContext(Dispatchers.IO) {
				// update the contents
				context.contentResolver.openOutputStream(newURI, "w")
					?.use { stream -> stream.write(bytes) }

				// update the file meta data
				context.contentResolver.update(newURI, updatedMetaData, null, null)
			}
			Result.success(newURI.toString())
		} catch (e: IOException) {
			// delete the uri if we are unable to save the content
			withContext(NonCancellable) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
					context.contentResolver.delete(newURI, null)
				else context.contentResolver.delete(newURI, null, null)
			}
			Result.failure(e)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}
}
