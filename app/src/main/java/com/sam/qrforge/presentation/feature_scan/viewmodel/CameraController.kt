package com.sam.qrforge.presentation.feature_scan.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.asFlow
import com.sam.qrforge.presentation.common.utils.CoroutineLifecycleOwner
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState
import com.sam.qrforge.presentation.feature_scan.state.CameraFocusState
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class CameraController(private val context: Context) {

	private val _isCameraReady = MutableStateFlow(false)
	private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
	val surface = _surfaceRequest.asStateFlow()

	private val _focusState = MutableStateFlow<CameraFocusState>(CameraFocusState.Unspecified)
	val focusState = _focusState.asStateFlow()

	@OptIn(ExperimentalCoroutinesApi::class)
	val isCameraFlashing = _isCameraReady.filter { it }
		.flatMapLatest { _cameraInfo?.torchState?.asFlow() ?: emptyFlow() }
		.map { state -> state == TorchState.ON }

	@OptIn(ExperimentalCoroutinesApi::class)
	val cameraZoom = _isCameraReady.filter { it }
		.flatMapLatest { _cameraInfo?.zoomState?.asFlow() ?: emptyFlow() }
		.map { state ->
			CameraZoomState(
				zoomRatio = { state.zoomRatio },
				maxZoomRatio = state.maxZoomRatio,
				minZoomRatio = state.minZoomRatio
			)
		}


	private var _meteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
	private var _cameraControl: CameraControl? = null
	private var _cameraInfo: CameraInfo? = null

	private var _isCaptureProgressSupported: Boolean = false
	val isCaptureProgressSupported: Boolean
		get() = _isCaptureProgressSupported

	private val cameraPreviewUseCase = Preview.Builder().build()
		.apply {
			setSurfaceProvider { request ->
				_surfaceRequest.update { request }
				_meteringPointFactory = SurfaceOrientedMeteringPointFactory(
					request.resolution.width.toFloat(),
					request.resolution.height.toFloat()
				)
			}
		}

	private val cameraCaptureUseCase = ImageCapture.Builder()
		.setOutputFormat(ImageCapture.OUTPUT_FORMAT_JPEG)
		.setJpegQuality(80)
		.setPostviewEnabled(true)
		.build()

	private val imageAnalyzerUseCase = ImageAnalysis.Builder()
		.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_NV21)
		.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
		.build()

	fun setAnalyzer(analyzer: ImageAnalysis.Analyzer) {
		val executor = ContextCompat.getMainExecutor(context)
		imageAnalyzerUseCase.setAnalyzer(executor, analyzer)
	}

	fun clearAnalyzer() = imageAnalyzerUseCase.clearAnalyzer()

	suspend fun prepareCameraInstance(scope: CoroutineScope) {
		val useCases = UseCaseGroup.Builder()
			.addUseCase(cameraPreviewUseCase)
			.addUseCase(cameraCaptureUseCase)
			.addUseCase(imageAnalyzerUseCase)
			.build()

		val lifecycle = CoroutineLifecycleOwner(scope.coroutineContext)
		val cameraProvider = ProcessCameraProvider.Companion.awaitInstance(context)

		val camera = cameraProvider.bindToLifecycle(
			lifecycle, CameraSelector.DEFAULT_BACK_CAMERA, useCases
		)

		_cameraControl = camera.cameraControl
		_cameraInfo = camera.cameraInfo
		val capabilities = ImageCapture.getImageCaptureCapabilities(camera.cameraInfo)
		_isCaptureProgressSupported = capabilities.isCaptureProcessProgressSupported

		_isCameraReady.update { true }
		try {
			// suspend this coroutine for until cancellation
			awaitCancellation()
		} finally {
			imageAnalyzerUseCase.clearAnalyzer()
			cameraProvider.unbindAll()
		}
	}

	@androidx.annotation.OptIn(ExperimentalGetImage::class)
	suspend fun captureImage(
		initialState: CameraCaptureState? = null,
		onStateChange: (CameraCaptureState) -> Unit,
		onPreviewBitmap: (Bitmap) -> Unit,
		onImageCapture: suspend (Bitmap, rotationAmount: Int) -> Unit,
		onError: suspend (Exception) -> Unit,
		cleanUp: () -> Unit = {},
	) {
		val state = initialState ?: CameraCaptureState()

		try {
			val imageProxy = cameraCaptureUseCase.takePicture(
				onCaptureStarted = { onStateChange(state.copy(isCapturing = true)) },
				onCaptureProcessProgressed = { progress ->
					val progressRatio = progress.toFloat() / 100
					onStateChange(state.copy(captureProgress = { progressRatio }))
				},
				onPostviewBitmapAvailable = onPreviewBitmap,
			)
			// call safe
			imageProxy.image ?: run {
				imageProxy.close()
				return@captureImage
			}
			// suspend the image working
			onImageCapture(imageProxy.toBitmap(), imageProxy.imageInfo.rotationDegrees)
			imageProxy.close()
		} catch (e: Exception) {
			e.printStackTrace()
			onError(e)
		} finally {
			cleanUp()
		}
	}

	fun onToggleFlash(): Boolean {
		if (_cameraInfo?.hasFlashUnit() == false) return false
		val isTorchOn = _cameraInfo?.torchState?.value == TorchState.ON
		_cameraControl?.enableTorch(!isTorchOn)
		return true
	}

	fun onZoomLevelChange(zoom: Float, isRelative: Boolean = false) {
		val controller = _cameraControl ?: return
		val currentZoom = _cameraInfo?.zoomState?.value?.zoomRatio ?: return

		// async cancel the current focus point and set the zoom
		controller.cancelFocusAndMetering()
		controller.setZoomRatio(if (isRelative) zoom * currentZoom else zoom)
	}

	suspend fun onTapToFocus(offset: Offset) {
		val controller = _cameraControl ?: return
		val point = _meteringPointFactory?.createPoint(offset.x, offset.y) ?: return
		_focusState.update {
			CameraFocusState.Specified(
				coordinates = offset,
				status = CameraFocusState.FocusStatus.RUNNING
			)
		}

		val focusPoint = FocusMeteringAction.Builder(point).build()

		val status = try {
			val focusResult = controller.startFocusAndMetering(focusPoint)
				.await()
			if (focusResult?.isFocusSuccessful == true) CameraFocusState.FocusStatus.SUCCESS
			else CameraFocusState.FocusStatus.FAILURE

		} catch (_: CameraControl.OperationCanceledException) {
			CameraFocusState.FocusStatus.CANCELLED
		}

		_focusState.update { state ->
			CameraFocusState.Specified(coordinates = offset, status = status)
		}
	}

	fun cameraCleanUp() {
		_isCameraReady.update { false }
		_cameraControl = null
		_cameraInfo = null
	}
}