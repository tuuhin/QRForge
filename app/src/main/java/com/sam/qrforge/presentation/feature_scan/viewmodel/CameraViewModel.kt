package com.sam.qrforge.presentation.feature_scan.viewmodel

import android.app.Application
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.MutatorMutex
import androidx.compose.ui.geometry.Offset
import androidx.concurrent.futures.await
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.CameraFocusState
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.presentation.feature_scan.state.CaptureType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val TAG = "CAMERA_CONTROLLER"

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewmodel(private val application: Application) : AndroidViewModel(application) {

	private val _isCameraReady = MutableStateFlow(false)

	private val _surface = MutableStateFlow<SurfaceRequest?>(null)
	val surfaceRequest = _surface.asStateFlow()

	private val _focusState = MutableStateFlow<CameraFocusState>(CameraFocusState.Unspecified)
	val focusState = _focusState.asStateFlow()

	private val _captureType = MutableStateFlow(CaptureType.AUTO)
	val captureType = _captureType.asStateFlow()

	val isTorchEnabled = _isCameraReady.filter { it }
		.flatMapLatest { _cameraInfo?.torchState?.asFlow() ?: emptyFlow() }
		.map { state -> state == TorchState.ON }.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000L),
			initialValue = false
		)

	val zoomLevel = _isCameraReady.filter { it }
		.flatMapLatest { _cameraInfo?.zoomState?.asFlow() ?: emptyFlow() }
		.map { state ->
			CameraZoomState(
				zoomRatio = { state.zoomRatio },
				maxZoomRatio = state.maxZoomRatio,
				minZoomRatio = state.minZoomRatio
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000L),
			initialValue = CameraZoomState()
		)

	private var _meteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
	private var _cameraControl: CameraControl? = null
	private var _cameraInfo: CameraInfo? = null

	private val _uiMutex = MutatorMutex()

	private val _cameraPreviewUseCase = Preview.Builder().build()
		.apply {
			setSurfaceProvider { request ->
				_surface.update { request }
				_meteringPointFactory = SurfaceOrientedMeteringPointFactory(
					request.resolution.width.toFloat(),
					request.resolution.height.toFloat()
				)
			}
		}

	private val _captureUseCase = ImageCapture.Builder().build()

	private val _analysisUseCase = ImageAnalysis.Builder()
		.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
		.build()

	private val _cameraUseCaseGroup = UseCaseGroup.Builder()
		.addUseCase(_cameraPreviewUseCase)
		.addUseCase(_captureUseCase)
		.addUseCase(_analysisUseCase)
		.build()

	private val _uiEvent = MutableSharedFlow<UIEvent>()
	val uiEvents = _uiEvent.asSharedFlow()

	fun onEvent(event: CameraControllerEvents) {
		when (event) {
			CameraControllerEvents.BindCamera -> bindCamera()
			CameraControllerEvents.UnBindCamera -> unBindCamera()
			is CameraControllerEvents.OnZoomLevelChange -> onZoomLevelChange(
				event.zoom,
				event.isRelative
			)

			is CameraControllerEvents.TapToFocus -> onTapToFocus(event.offset)
			CameraControllerEvents.ToggleFlash -> onToggleFlash()
			is CameraControllerEvents.OnChangeCaptureMode -> _captureType.update { event.mode }
		}
	}

	private var _bindCameraJob: Job? = null

	private fun bindCamera() {
		_bindCameraJob = viewModelScope.launch {

			val cameraProvider = ProcessCameraProvider.awaitInstance(application)
			val scopedLifecycle = CoroutineLifecycleOwner(this.coroutineContext)
			val camera = cameraProvider.bindToLifecycle(
				scopedLifecycle, CameraSelector.DEFAULT_BACK_CAMERA, _cameraUseCaseGroup
			)
			_cameraControl = camera.cameraControl
			_cameraInfo = camera.cameraInfo

			_isCameraReady.update { true }
			try {
				// this will stop it from being finishing
				awaitCancellation()
			} finally {
				cameraProvider.unbindAll()
			}
		}
	}

	private fun unBindCamera() {
		_bindCameraJob?.invokeOnCompletion {
			_isCameraReady.update { false }
			_cameraControl = null
			_cameraInfo = null
		}
		_bindCameraJob?.cancel()
		_bindCameraJob = null
	}

	private fun onToggleFlash() = viewModelScope.launch {
		if (_cameraInfo?.hasFlashUnit() == false) return@launch

		val isOn = _cameraInfo?.torchState?.value == TorchState.ON
		_cameraControl?.enableTorch(!isOn)
	}

	private fun onZoomLevelChange(zoom: Float, isRelative: Boolean = false) =
		viewModelScope.launch {
			val controller = _cameraControl ?: return@launch
			val currentZoom = _cameraInfo?.zoomState?.value?.zoomRatio ?: return@launch

			_uiMutex.mutate {
				controller.cancelFocusAndMetering()
				controller.setZoomRatio(if (isRelative) zoom * currentZoom else zoom)
			}
		}

	private fun onTapToFocus(offset: Offset) = viewModelScope.launch {
		val point = _meteringPointFactory?.createPoint(offset.x, offset.y) ?: return@launch
		_focusState.update {
			CameraFocusState.Specified(
				coordinates = offset,
				status = CameraFocusState.FocusStatus.RUNNING
			)
		}
		val focusPoint = FocusMeteringAction.Builder(point).build()

		val status = try {
			val futures = _cameraControl?.startFocusAndMetering(focusPoint)
			val isSuccessful = futures?.await()?.isFocusSuccessful == true
			if (isSuccessful) CameraFocusState.FocusStatus.SUCCESS
			else CameraFocusState.FocusStatus.FAILURE

		} catch (_: CameraControl.OperationCanceledException) {
			CameraFocusState.FocusStatus.CANCELLED
		}

		_focusState.update {
			CameraFocusState.Specified(
				coordinates = offset,
				status = status
			)
		}
	}

	private inner class CoroutineLifecycleOwner(coroutineContext: CoroutineContext) :
		LifecycleOwner {

		val lifecycleRegistry = LifecycleRegistry(this).apply {
			currentState = Lifecycle.State.INITIALIZED
		}

		override val lifecycle: Lifecycle
			get() = lifecycleRegistry

		init {
			// get the job
			if (coroutineContext[Job]?.isActive == true) {
				lifecycleRegistry.currentState = Lifecycle.State.RESUMED
				Log.d(TAG, "Lifecycle Resumed")
				coroutineContext[Job]?.invokeOnCompletion {
					Log.d(TAG, "Lifecycle destroyed")
					lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
				}
			} else {
				Log.d(TAG, "Lifecycle destroyed job inactive")
				lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
			}
		}
	}
}