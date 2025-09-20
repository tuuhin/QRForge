package com.sam.qrforge.data.facade

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.sam.qrforge.domain.facade.QRValidatorFacade
import com.sam.qrforge.domain.models.GeneratedARGBQRModel

class QRValidatorFacadeImpl : QRValidatorFacade {

	private val _decoder by lazy { MultiFormatReader() }

	private val _decoderHints = mapOf(
		DecodeHintType.CHARACTER_SET to Charsets.UTF_8.toString(),
		DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
		DecodeHintType.TRY_HARDER to true,
		DecodeHintType.ALSO_INVERTED to true
	)

	override suspend fun isValid(rgbaModel: GeneratedARGBQRModel): Result<Boolean> {
		return try {

			// prepare the binary image
			val source = RGBLuminanceSource(rgbaModel.width, rgbaModel.height, rgbaModel.pixels)
			val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

			// the decoding process
			_decoder.decode(binaryBitmap, _decoderHints)
			Result.success(true)
		} catch (_: NotFoundException) {
			Result.success(false)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		} finally {
			// reset the decoder
			_decoder.reset()
		}
	}
}