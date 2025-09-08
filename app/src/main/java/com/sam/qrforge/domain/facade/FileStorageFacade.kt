package com.sam.qrforge.domain.facade

interface FileStorageFacade {

	suspend fun saveContentToShare(bytes: ByteArray): Result<String>

	suspend fun saveImageContentToStorage(bytes: ByteArray): Result<String>
}