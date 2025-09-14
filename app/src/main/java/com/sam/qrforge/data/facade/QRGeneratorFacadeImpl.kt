package com.sam.qrforge.data.facade

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.sam.qrforge.data.mappers.toModel
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class QRGeneratorFacadeImpl : QRGeneratorFacade {

	private val writer by lazy { MultiFormatWriter() }

	override suspend fun generate(data: QRContentModel, useHints: Boolean)
			: Result<GeneratedQRModel> {
		return withContext(Dispatchers.Default) {

			val hints = if (useHints) mapOf(
				EncodeHintType.CHARACTER_SET to StandardCharsets.UTF_8.toString(),
				EncodeHintType.MARGIN to 2,
				EncodeHintType.DATA_MATRIX_SHAPE to SymbolShapeHint.FORCE_SQUARE,
			) else emptyMap()

			try {
				val content = data.toQRString()
				val matrix = writer.encode(
					content,
					BarcodeFormat.QR_CODE,
					0,
					0,
					hints
				)
				Result.success(matrix.toModel())
			} catch (e: WriterException) {
				Result.failure(e)
			} catch (e: Exception) {
				Result.failure(e)
			}
		}
	}

	override suspend fun generate(contentString: String): Result<GeneratedQRModel> {
		return withContext(Dispatchers.Default) {
			try {
				val matrix = writer.encode(
					contentString,
					BarcodeFormat.QR_CODE,
					0,
					0,
				)
				Result.success(matrix.toModel())
			} catch (e: WriterException) {
				Result.failure(e)
			} catch (e: Exception) {
				Result.failure(e)
			}
		}
	}
}