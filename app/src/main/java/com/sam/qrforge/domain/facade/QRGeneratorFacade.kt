package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel

interface QRGeneratorFacade {

	suspend fun generate(data: QRContentModel, useHints: Boolean = true): Result<GeneratedQRModel>

	suspend fun generate(contentString: String): Result<GeneratedQRModel>
}