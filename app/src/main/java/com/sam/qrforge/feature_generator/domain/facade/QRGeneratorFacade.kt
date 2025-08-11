package com.sam.qrforge.feature_generator.domain.facade

import com.sam.qrforge.feature_generator.domain.models.EncoderSettings
import com.sam.qrforge.feature_generator.domain.models.ExportDimensions
import com.sam.qrforge.feature_generator.domain.models.GeneratedQRModel
import com.sam.qrforge.feature_generator.domain.models.qr.QRDataModel

interface QRGeneratorFacade {

	suspend fun generate(
		data: QRDataModel,
		settings: EncoderSettings = EncoderSettings(),
		dimension: ExportDimensions = ExportDimensions.Small
	): Result<GeneratedQRModel>
}