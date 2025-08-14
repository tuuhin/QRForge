package com.sam.qrforge.domain.facade

import com.sam.qrforge.domain.models.EncoderSettings
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel

interface QRGeneratorFacade {

	suspend fun generate(
		data: QRContentModel,
		settings: EncoderSettings = EncoderSettings(),
	): Result<GeneratedQRModel>
}