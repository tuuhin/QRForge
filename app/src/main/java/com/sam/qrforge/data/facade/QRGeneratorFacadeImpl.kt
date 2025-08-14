package com.sam.qrforge.data.facade

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.sam.qrforge.data.mappers.toEncoderHints
import com.sam.qrforge.data.mappers.toModel
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.EncoderSettings
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRGeneratorFacadeImpl : QRGeneratorFacade {

	private val writer by lazy { MultiFormatWriter() }

	override suspend fun generate(
		data: QRContentModel,
		settings: EncoderSettings,
	): Result<GeneratedQRModel> {
		return withContext(Dispatchers.Default) {
			try {
				val hints = settings.toEncoderHints()
				val content = data.toQRString()
				val matrix = writer.encode(
					content,
					BarcodeFormat.QR_CODE,
					0,
					0,
					hints,
				)
				Result.success(matrix.toModel())
			} catch (e: WriterException) {
				Result.failure(e)
			}
		}
	}
}