package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel

fun interface QRGeneratorFacade {

	suspend fun generate(data: QRContentModel): Result<GeneratedQRModel>
}