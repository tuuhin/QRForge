package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface FileStorageFacade {

	suspend fun saveContentToShare(bytes: ByteArray): Result<String>

	fun saveImageContentToStorage(
		bytes: ByteArray,
		dimensions: ExportDimensions = ExportDimensions.Medium,
		mimeType: ImageMimeTypes = ImageMimeTypes.PNG,
	): Flow<Resource<String, Exception>>
}