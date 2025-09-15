package com.sam.qrforge.presentation.feature_scan.viewmodel

import android.graphics.Bitmap
import androidx.compose.foundation.MutatorMutex
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.facade.QRImageAnalyzer
import com.sam.qrforge.domain.facade.QRScannerFacade
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.common.utils.toBytes
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.CameraControlsState
import com.sam.qrforge.presentation.feature_scan.state.CaptureType
import com.sam.qrforge.presentation.feature_scan.state.ImageAnalysisState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
	private val controller: CameraController,
	private val qrAnalyzer: QRImageAnalyzer,
	private val decoder: QRScannerFacade,
) : AppViewModel() {

	val cameraControlState = combine(
		controller.surface,
		controller.focusState,
		controller.cameraZoom,
		controller.isCameraFlashing
	) { surface, focus, zoom, flash ->
		CameraControlsState(
			surfaceRequest = surface,
			focusState = focus,
			isFlashEnabled = flash,
			zoomState = zoom
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000L),
		initialValue = CameraControlsState()
	)

	private val _cameraCaptureState = MutableStateFlow(CameraCaptureState())
	val cameraCaptureState = _cameraCaptureState.asStateFlow()

	private val _analyzerState = MutableStateFlow(ImageAnalysisState())
	val imageAnalyzerState = _analyzerState.asStateFlow()

	private val _localAnalysis = MutableStateFlow<QRContentModel?>(null)
	val analysisResult: SharedFlow<QRContentModel?>
		get() {
			val imageAnalyzer = qrAnalyzer.resultAnalysis
				.filter { it.isSuccess }.map { it.getOrNull() }

			return merge(imageAnalyzer, _localAnalysis)
				.onStart { prepareImageAnalyzers() }
				.distinctUntilChanged()
				.shareIn(
					scope = viewModelScope,
					started = SharingStarted.Lazily,
				)
		}

	private var _bindCameraJob: Job? = null
	private val _uiMutex = MutatorMutex()

	private val _uiEvent = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvent

	fun onCameraEvents(event: CameraControllerEvents) {
		when (event) {
			CameraControllerEvents.BindCamera -> bindCamera()
			CameraControllerEvents.UnBindCamera -> unBindCamera()
			CameraControllerEvents.ToggleFlash -> controller.onToggleFlash()
			is CameraControllerEvents.OnZoomLevelChange -> viewModelScope.launch {
				_uiMutex.mutate {
					controller.onZoomLevelChange(
						event.zoom,
						event.isRelative
					)
				}
			}

			is CameraControllerEvents.TapToFocus -> viewModelScope.launch {
				controller.onTapToFocus(event.offset)
			}

			CameraControllerEvents.CaptureImage -> captureImage()
			is CameraControllerEvents.OnSelectImageURI -> onGalleryImageSelected(event.uri)
			is CameraControllerEvents.OnChangeCaptureMode -> _analyzerState.update { state ->
				state.copy(captureType = event.type)
			}

			CameraControllerEvents.OnClearAnalysisResult -> _localAnalysis.update { null }
		}
	}

	private fun bindCamera() {
		_bindCameraJob = viewModelScope.launch {
			controller.prepareCameraInstance(this)
		}
	}

	private fun unBindCamera() {
		_bindCameraJob?.invokeOnCompletion {
			controller.cameraCleanUp()
		}
		_bindCameraJob?.cancel()
		_bindCameraJob = null
	}

	private fun prepareImageAnalyzers() {
		_analyzerState.map { it.captureType }
			.distinctUntilChanged()
			.onEach { type ->
				when (type) {
					CaptureType.AUTO -> controller.setAnalyzer(qrAnalyzer)
					CaptureType.MANUAL -> controller.clearAnalyzer()
				}
				_analyzerState.update { state ->
					state.copy(isAnalyserSet = type == CaptureType.AUTO, isAnalysing = false)
				}
			}.launchIn(viewModelScope)
	}

	private fun onGalleryImageSelected(uriString: String) = viewModelScope.launch {
		_analyzerState.update { state -> state.copy(isAnalysing = true) }
		val results = decoder.decodeFromFile(uriString)

		results.fold(
			onSuccess = { model -> _localAnalysis.update { model } },
			onFailure = { err ->
				val message = err.message ?: "Some error"
				_uiEvent.emit(UIEvent.ShowToast(message))
			},
		)
		_analyzerState.update { state -> state.copy(isAnalysing = false) }
	}

	private fun captureImage() = viewModelScope.launch {
		controller.captureImage(
			onStateChange = { state ->
				// if capture progress is supported then this will increase progress
				_cameraCaptureState.update { state.copy(canPropagateProgress = controller.isCaptureProgressSupported) }
			},
			onPreviewBitmap = { bitmap ->
				// if post view mode is supported then this call back will provide a image preview
				val previous = _cameraCaptureState
					.getAndUpdate { state -> state.copy(postCapturePreview = bitmap) }
				previous.postCapturePreview?.recycle()
			},
			onImageCapture = ::analyzeBitmap,
			onError = { err ->
				val message = err.message ?: "SOME ERROR"
				_uiEvent.emit(UIEvent.ShowSnackBar(message))
			},
			cleanUp = {
				val previous = _cameraCaptureState
					.getAndUpdate { CameraCaptureState(canPropagateProgress = controller.isCaptureProgressSupported) }
				previous.postCapturePreview?.recycle()
			}
		)
	}

	private fun analyzeBitmap(bitmap: Bitmap, rotate: Int) = viewModelScope.launch {
		_analyzerState.update { state -> state.copy(isAnalysing = true) }
		val bytes = bitmap.asImageBitmap().toBytes()
		val results = decoder.decodeAsBitMap(bytes, bitmap.width, bitmap.height, rotate)
		results.fold(
			onSuccess = { model -> _localAnalysis.update { model } },
			onFailure = { err ->
				val message = err.message ?: "Some error"
				_uiEvent.emit(UIEvent.ShowToast(message))
			},
		)
		_analyzerState.update { state -> state.copy(isAnalysing = false) }
	}

	override fun onCleared() {
		qrAnalyzer.cleanUp()
		super.onCleared()
	}
}