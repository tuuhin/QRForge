package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.models.qr.QRContentModel

interface QRScannerFacade {

	suspend fun decodeFromFile(uri: String): Result<QRContentModel>

	suspend fun decodeAsNV21Source(
		bytes: ByteArray,
		width: Int,
		height: Int,
		rotate: Int,
	): Result<QRContentModel>

	suspend fun decodeAsBitMap(
		bytes: ByteArray,
		width: Int,
		height: Int,
		rotate: Int,
	): Result<QRContentModel>
}