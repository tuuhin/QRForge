package com.sam.qrforge.domain.facade

fun interface SaveGeneratedQRFacade {

	suspend fun prepareFileToShare(bytes: ByteArray): Result<String>
}