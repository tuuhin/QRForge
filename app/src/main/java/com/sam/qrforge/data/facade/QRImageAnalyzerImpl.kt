package com.sam.qrforge.data.facade

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.sam.qrforge.domain.facade.QRImageAnalyzer
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.facade.exception.NoQRCodeFoundException
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val THROTTLE_TIME = 200.milliseconds

class QRImageAnalyzerImpl(private val scanner: QRScannerFacade) : QRImageAnalyzer {

	@OptIn(ExperimentalCoroutinesApi::class)
	private val dispatcher = Dispatchers.IO.limitedParallelism(50)
	private val _scope = CoroutineScope(dispatcher + SupervisorJob())

	private val _result = MutableSharedFlow<Result<QRContentModel>>()
	override val resultAnalysis: Flow<Result<QRContentModel>>
		get() = _result

	@ExperimentalGetImage
	override fun analyze(image: ImageProxy) {
		_scope.launch {
			val imageObject = image.image ?: return@launch
			val rotation = image.imageInfo.rotationDegrees
			try {
				val inputImage = InputImage.fromMediaImage(imageObject, rotation)
				val result = scanner.decodeQR(inputImage)

				result.fold(
					onSuccess = { _result.emit(Result.success(it)) },
					onFailure = { err ->
						if (err is NoQRCodeFoundException) return@fold
						_result.emit(Result.failure(Exception(err)))
					},
				)
				delay(THROTTLE_TIME)
			} catch (e: Exception) {
				if (e is CancellationException) throw e
			}
		}.invokeOnCompletion {
			image.close()
		}
	}

	override fun cleanUp() = _scope.coroutineContext.cancelChildren()
}