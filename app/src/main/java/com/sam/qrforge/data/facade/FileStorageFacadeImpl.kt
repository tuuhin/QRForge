package com.sam.qrforge.data.facade

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.sam.qrforge.data.utils.hasWriteStoragePermission
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.exception.CannotCreateFileException
import com.sam.qrforge.domain.facade.exception.CannotScaleBitmapException
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

private const val TAG = "FILE_STORAGE"

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

	override fun saveImageContentToStorage(
		bytes: ByteArray,
		dimensions: ExportDimensions,
		mimeType: ImageMimeTypes,
	): Flow<Resource<String, Exception>> = flow {

		val extension = when (mimeType) {
			ImageMimeTypes.JPEG -> "jpg"
			ImageMimeTypes.PNG -> "png"
		}

		val displayName = "exported_${abs(Random.nextInt() / 100)}.$extension"

		val compressFormat = when (mimeType) {
			ImageMimeTypes.JPEG -> Bitmap.CompressFormat.JPEG
			ImageMimeTypes.PNG -> Bitmap.CompressFormat.PNG
		}

		emit(Resource.Loading)

		val scaledBitmap = try {
			withContext(Dispatchers.IO) {
				val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
				bitmap.scale(dimensions.sizeInPx, dimensions.sizeInPx)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
		if (scaledBitmap == null) {
			emit(Resource.Error(CannotScaleBitmapException()))
			return@flow
		}

		try {
			currentCoroutineContext().ensureActive()
			// do the work
			val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				saveBitmapToScopedStorage(
					displayName = displayName,
					bitmap = scaledBitmap,
					compressFormat = compressFormat,
					mimeType = mimeType.mimeType,
				)
			} else saveBitmapToPublicDirectory(
				outputFileName = displayName,
				bitmap = scaledBitmap,
				compressFormat = compressFormat
			)
			emit(result)

		} finally {
			scaledBitmap.recycle()
		}
	}

	private suspend fun saveBitmapToPublicDirectory(
		outputFileName: String,
		bitmap: Bitmap,
		compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
	): Resource<String, Exception> {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || !context.hasWriteStoragePermission)
			return Resource.Error(Exception("Invalid API or permission missing"))

		val picturesDir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

		val appDir = File(picturesDir, "QRForge")
			.apply { if (!exists()) mkdirs() }

		val outFile = File(appDir, outputFileName)
		try {
			withContext(Dispatchers.IO) {
				// create the file
				if (!outFile.exists()) outFile.createNewFile()
				outFile.outputStream().use { stream ->
					bitmap.compress(compressFormat, 100, stream)
				}
			}
			return Resource.Success(outFile.toString())
		} catch (e: CancellationException) {
			Log.d(TAG, "CANCELLATION EXCEPTION")
			withContext(NonCancellable) {
				outFile.delete()
			}
			throw e
		} catch (e: Exception) {
			e.printStackTrace()
			return Resource.Error(e)
		}
	}

	private suspend fun saveBitmapToScopedStorage(
		displayName: String,
		bitmap: Bitmap,
		compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
		mimeType: String,
	): Resource<String, Exception> {

		val contentValues = ContentValues().apply {
			put(
				MediaStore.Images.ImageColumns.RELATIVE_PATH,
				Environment.DIRECTORY_PICTURES + File.separator + "QRForge"
			)
			put(MediaStore.Images.ImageColumns.DISPLAY_NAME, displayName)
			put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType)
			put(MediaStore.Images.ImageColumns.IS_PENDING, 1)
		}

		val updatedMetaData = ContentValues().apply {
			put(MediaStore.Images.ImageColumns.IS_PENDING, 0)
		}

		// will return if we cannot create the uri
		val newURI = withContext(Dispatchers.IO) {
			context.contentResolver.insert(imagesParentURI, contentValues)
		} ?: return Resource.Error(CannotCreateFileException())

		return try {
			// then start updating the flow
			withContext(Dispatchers.IO) {
				// update the contents
				context.contentResolver.openOutputStream(newURI, "w")
					?.use { stream ->
						bitmap.compress(compressFormat, 100, stream)
					}
				// update the file meta data
				context.contentResolver.update(newURI, updatedMetaData, null, null)
			}
			Resource.Success(newURI.toString())
		} catch (e: CancellationException) {
			Log.d(TAG, "Cancellation exception")
			// delete the uri if we are unable to save the content
			withContext(NonCancellable) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
					context.contentResolver.delete(newURI, null)
				else context.contentResolver.delete(newURI, null, null)
			}
			throw e
		} catch (e: Exception) {
			Resource.Error(e)
		}
	}
}