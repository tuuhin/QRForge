package com.sam.qrforge.feature_generator.data.facade

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.sam.qrforge.feature_generator.data.util.toEncoderHints
import com.sam.qrforge.feature_generator.data.util.toModel
import com.sam.qrforge.feature_generator.domain.facade.QRGeneratorFacade
import com.sam.qrforge.feature_generator.domain.models.EncoderSettings
import com.sam.qrforge.feature_generator.domain.models.ExportDimensions
import com.sam.qrforge.feature_generator.domain.models.GeneratedQRModel
import com.sam.qrforge.feature_generator.domain.models.qr.QRDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRGeneratorFacadeImpl : QRGeneratorFacade {

	private val writer by lazy { MultiFormatWriter() }

	override suspend fun generate(
		data: QRDataModel,
		settings: EncoderSettings,
		dimension: ExportDimensions
	): Result<GeneratedQRModel> {
		return withContext(Dispatchers.Default) {
			try {
				val hints = settings.toEncoderHints()
				val content = data.toQRString()
				val matrix = writer.encode(
					content,
					BarcodeFormat.QR_CODE,
					dimension.widthInPx,
					dimension.heightInPx,
					hints,
				)
				Result.success(matrix.toModel())
			} catch (e: WriterException) {
				Result.failure(e)
			}
		}
	}
}