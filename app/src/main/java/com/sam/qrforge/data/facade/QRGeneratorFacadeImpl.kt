package com.sam.qrforge.data.facade

import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.sam.qrforge.data.mappers.toModel
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.util.PlatformCoroutineDispatchers
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class QRGeneratorFacadeImpl(
    private val dispatchers: PlatformCoroutineDispatchers,
) : QRGeneratorFacade {

    private val writer by lazy { MultiFormatWriter() }

    override suspend fun generate(
        data: QRContentModel,
        useHints: Boolean,
    ): Result<GeneratedQRModel> {
        return withContext(dispatchers.default) {

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
                    hints,
                )
                Result.success(matrix.toModel())
            } catch (e: WriterException) {
                Log.e(TAG, "FAILED TO PERFORM ENCODING")
                Result.failure(e)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "SOMETHING WENT WRONG ", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun generate(contentString: String): Result<GeneratedQRModel> {
        return withContext(dispatchers.default) {
            try {
                val matrix = writer.encode(
                    contentString,
                    BarcodeFormat.QR_CODE,
                    0,
                    0,
                )
                Result.success(matrix.toModel())
            } catch (e: WriterException) {
                Log.e(TAG, "FAILED TO PERFORM ENCODING")
                Result.failure(e)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "SOMETHING WENT WRONG ", e)
                Result.failure(e)
            }
        }
    }

    companion object {
        private const val TAG = "QRGeneratorFacade"
    }
}
