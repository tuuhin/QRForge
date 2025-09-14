package com.sam.qrforge.data.facade

import android.util.Log
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.sam.qrforge.data.mappers.toQRModel
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.facade.exception.NoQRCodeFoundException
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

private const val TAG = "QR_DECODER"

class QRScannerFacadeImpl : QRScannerFacade {

	private val _options = BarcodeScannerOptions.Builder()
		.setBarcodeFormats(Barcode.FORMAT_QR_CODE)
		.build()

	private val _client by lazy { BarcodeScanning.getClient(_options) }

	override suspend fun decodeQR(image: InputImage): Result<QRContentModel> {
		return try {
			val result = decode(image) ?: return Result.failure(NoQRCodeFoundException())
			Result.success(result)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun decodeQRAsFlow(image: InputImage): Flow<Resource<QRContentModel, Throwable>> {
		return flow {
			emit(Resource.Loading)
			val result = decodeQR(image)

			result.fold(
				onSuccess = { model -> emit(Resource.Success(model)) },
				onFailure = { err ->
					val error = err as? Exception ?: Exception("Unknown Exception")
					emit(Resource.Error(error))
				},
			)
		}
	}


	@OptIn(ExperimentalCoroutinesApi::class)
	private suspend fun decode(image: InputImage): QRContentModel? =
		suspendCancellableCoroutine { cont ->
			_client.process(image).apply {
				addOnCompleteListener {
					addOnSuccessListener { barcodes ->
						val first = barcodes.firstOrNull()?.rawValue?.toQRModel()
						cont.resume(first, onCancellation = null)
					}
					addOnFailureListener { exp ->
						if (exp is MlKitException) {
							Log.i(TAG, "ML_KIT_EXCEPTION")
							cont.cancel()
							return@addOnFailureListener
						}
						cont.resumeWithException(exp)
					}
				}
				addOnCanceledListener(cont::cancel)
			}
		}
}