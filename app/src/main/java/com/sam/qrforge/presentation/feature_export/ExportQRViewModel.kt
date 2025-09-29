package com.sam.qrforge.presentation.feature_export

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.data.mappers.toCompressedByteArray
import com.sam.qrforge.data.mappers.toRGBAModel
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRValidatorFacade
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenState
import com.sam.qrforge.presentation.feature_export.state.VerificationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExportQRViewModel(
	private val fileFacade: FileStorageFacade,
	private val validator: QRValidatorFacade,
	private val analyticsLogger: AnalyticsTracker
) : AppViewModel() {

	private val _decoration =
		MutableStateFlow<QRDecorationOption>(QRDecorationOption.QRDecorationOptionBasic())
	val selectedDecoration = _decoration.asStateFlow()

	private val _dimension = MutableStateFlow(ExportDimensions.Medium)
	private val _mimetype = MutableStateFlow(ImageMimeTypes.PNG)
	private val _verificationState = MutableStateFlow(VerificationState.NOT_VERIFIED)
	private val _isExportRunning = MutableStateFlow(false)

	private val _uiMutex = MutatorMutex()

	val exportScreenState = combine(
		_dimension,
		_mimetype,
		_verificationState,
		_isExportRunning,
	) { exportDimension, mimeType, verificationState, isExporting ->
		ExportQRScreenState(
			verificationState = verificationState,
			canExport = !isExporting,
			exportDimensions = exportDimension,
			selectedMimeType = mimeType,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(2_000L),
		initialValue = ExportQRScreenState()
	)

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	private val _activityEvents = MutableSharedFlow<LaunchActivityEvent>()
	val activityEvents = _activityEvents.asSharedFlow()

	private var _exportJob: Job? = null
	private var _imageToExport: ImageBitmap? = null

	fun onEvent(event: ExportQRScreenEvents) {
		when (event) {
			is ExportQRScreenEvents.OnDecorationChange -> viewModelScope.launch {
				_uiMutex.mutate {
					_decoration.update { event.decoration }
				}
				_verificationState.update { VerificationState.NOT_VERIFIED }
			}

			is ExportQRScreenEvents.OnQRTemplateChange -> viewModelScope.launch {
				_uiMutex.mutate(priority = MutatePriority.PreventUserInput) {
					_decoration.update { state -> state.swtichTemplate(event.template) }
				}
				_verificationState.update { VerificationState.NOT_VERIFIED }
			}

			is ExportQRScreenEvents.OnExportMimeTypeChange -> _mimetype.update { event.mimeType }
			is ExportQRScreenEvents.OnExportDimensionChange -> _dimension.update { event.dimen }
			ExportQRScreenEvents.OnExportBitmap -> {
				_exportJob?.cancel()
				_exportJob = onStartExport()
			}

			ExportQRScreenEvents.OnCancelExport -> onCancelExport()
			is ExportQRScreenEvents.OnVerifyBitmap -> verifyQRBitmap(event.bitmap)
			ExportQRScreenEvents.OnResetVerify -> _verificationState.update { VerificationState.NOT_VERIFIED }
			ExportQRScreenEvents.OnDismissWarning -> _verificationState.update { VerificationState.NOT_VERIFIED }
		}
	}

	private fun onPreviewSaveURI(uri: String) = viewModelScope.launch {
		analyticsLogger.logEvent(AnalyticsEvent.EXPORT_QR_PREVIEWED)
		_activityEvents.emit(LaunchActivityEvent.PreviewImageURI(uri))
	}

	private fun onStartExport() = viewModelScope.launch {
		val bitmap = _imageToExport ?: return@launch
		if (_verificationState.value != VerificationState.VERIFIED) {
			_uiEvents.emit(UIEvent.ShowToast("Verification Required"))
			return@launch
		}

		val compressedBytes = bitmap.toCompressedByteArray()

		val fileSaveFlow = fileFacade.saveImageContentToStorage(
			bytes = compressedBytes,
			dimensions = _dimension.value,
			mimeType = _mimetype.value
		)

		fileSaveFlow
			.onStart { _isExportRunning.update { true } }
			.onCompletion {
				_isExportRunning.update { false }
				_verificationState.update { VerificationState.NOT_VERIFIED }
			}
			.onEach { res ->
				when (res) {
					is Resource.Error -> {
						analyticsLogger.logEvent(
							AnalyticsEvent.EXPORT_QR_EVENT,
							mapOf(
								AnalyticsParams.IS_SUCCESSFUL to false,
								AnalyticsParams.ERROR_NAME to res.error::javaClass.name
							)
						)
						val message = res.message ?: res.error.message ?: "Cannot upload"
						_uiEvents.emit(UIEvent.ShowSnackBar(message))
					}

					is Resource.Success -> {
						analyticsLogger.logEvent(
							AnalyticsEvent.EXPORT_QR_EVENT,
							mapOf(
								AnalyticsParams.IS_SUCCESSFUL to true,
								AnalyticsParams.EXPORT_QR_TEMPLATE to _decoration.value.templateType
							)
						)
						val event = UIEvent.ShowSnackBarWithAction(
							message = "Successfully Exported",
							actionText = "Preview",
							action = { onPreviewSaveURI(res.data) },
						)
						_uiEvents.emit(event)
					}

					else -> {}
				}
			}.launchIn(this)
	}

	private fun onCancelExport() {
		analyticsLogger.logEvent(AnalyticsEvent.EXPORT_QR_CANCELED)
		_exportJob?.cancel()
		_exportJob = null
	}

	private fun verifyQRBitmap(bitmap: ImageBitmap) {
		_verificationState.update { VerificationState.VERIFYING }

		viewModelScope.launch {
			// check certification result
			val result = validator.isValid(bitmap.toRGBAModel())

			if (result.isFailure) {
				_uiEvents.emit(UIEvent.ShowSnackBar("Cannot verify the given QR"))
				_verificationState.update { VerificationState.NOT_VERIFIED }
				return@launch
			}

			analyticsLogger.logEvent(
				AnalyticsEvent.EXPORT_QR_VERIFIED,
				mapOf(
					AnalyticsParams.IS_SUCCESSFUL to result.isSuccess,
					AnalyticsParams.EXPORT_QR_TEMPLATE to _decoration.value.templateType
				)
			)

			val isVerified = result.getOrNull() ?: false
			val newState = if (!isVerified) VerificationState.FAILED
			else {
				_imageToExport = bitmap
				VerificationState.VERIFIED
			}
			_verificationState.update { newState }
		}
	}

	override fun onCleared() {
		_imageToExport = null
		super.onCleared()
	}
}