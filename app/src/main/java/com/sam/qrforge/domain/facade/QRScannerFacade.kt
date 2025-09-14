package com.sam.qrforge.domain.facade

import com.google.mlkit.vision.common.InputImage
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface QRScannerFacade {

	suspend fun decodeQR(image: InputImage): Result<QRContentModel>

	suspend fun decodeQRAsFlow(image: InputImage): Flow<Resource<QRContentModel, Throwable>>
}