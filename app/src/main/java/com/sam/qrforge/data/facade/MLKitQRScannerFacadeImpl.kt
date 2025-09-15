package com.sam.qrforge.data.facade

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.sam.qrforge.data.mappers.toQRModel
import com.sam.qrforge.data.utils.await
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.facade.exception.FileNotFoundException
import com.sam.qrforge.domain.facade.exception.NoQRCodeFoundException
import com.sam.qrforge.domain.models.qr.QRContentModel
import java.io.IOException

class MLKitQRScannerFacadeImpl(private val context: Context) : QRScannerFacade {

	private val _options = BarcodeScannerOptions.Builder()
		.setBarcodeFormats(Barcode.FORMAT_QR_CODE)
		.build()

	private val _client by lazy { BarcodeScanning.getClient(_options) }

	private suspend fun decodeQR(image: InputImage): Result<QRContentModel> {
		return try {
			val barcodes = _client.process(image).await()
			val result = barcodes.firstOrNull()?.rawValue?.toQRModel()
				?: return Result.failure(NoQRCodeFoundException())
			Result.success(result)
		} catch (e: MlKitException) {
			val exception = when (e.errorCode) {
				MlKitException.UNAVAILABLE -> Exception("ML Kit not found")
				MlKitException.NETWORK_ISSUE -> Exception("Internet connection not found")
				MlKitException.NOT_ENOUGH_SPACE -> Exception("Cannot install model")
				else -> e
			}
			Result.failure(exception)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun decodeAsBitMap(
		bytes: ByteArray,
		width: Int,
		height: Int,
		rotate: Int,
	): Result<QRContentModel> {
		return try {
			val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
				?: return Result.failure(Exception("Invalid bytes"))
			val image = InputImage.fromBitmap(bitmap, rotate)
			decodeQR(image)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(Exception("Unknown Exception"))
		}
	}

	override suspend fun decodeAsNV21Source(
		bytes: ByteArray,
		width: Int,
		height: Int,
		rotate: Int,
	): Result<QRContentModel> {
		return try {
			val image = InputImage.fromByteArray(
				bytes,
				width,
				height,
				rotate,
				InputImage.IMAGE_FORMAT_NV21
			)
			decodeQR(image)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(Exception("Some other exception"))
		}
	}

	override suspend fun decodeFromFile(uri: String): Result<QRContentModel> {
		return try {
			val image = InputImage.fromFilePath(context, uri.toUri())
			decodeQR(image)
		} catch (_: IOException) {
			Result.failure(FileNotFoundException())
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		}
	}
}