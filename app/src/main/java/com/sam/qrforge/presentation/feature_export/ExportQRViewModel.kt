package com.sam.qrforge.presentation.feature_export

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.data.mappers.toCompressedByteArray
import com.sam.qrforge.data.mappers.toRGBAModel
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class ExportQRViewModel(
	private val fileFacade: FileStorageFacade,
	private val validator: QRValidatorFacade,
) : AppViewModel() {

	private val _decoration =
		MutableStateFlow<QRDecorationOption>(QRDecorationOption.QRDecorationOptionBasic())
	val selectedDecoration = _decoration.asStateFlow()

	private val _dimension = MutableStateFlow(ExportDimensions.Medium)
	private val _mimetype = MutableStateFlow(ImageMimeTypes.PNG)
	private val _isExportRunning = MutableStateFlow(false)
	private val _isTooMuchEdit = MutableStateFlow(false)

	val exportScreenState = combine(
		_dimension,
		_mimetype,
		_isExportRunning,
		_isTooMuchEdit
	) { exportDimension, mimeType, isExporting, validation ->
		ExportQRScreenState(
			isExporting = isExporting,
			exportDimensions = exportDimension,
			selectedMimeType = mimeType,
			showTooMuchEdit = validation
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

	fun onEvent(event: ExportQRScreenEvents) {
		when (event) {
			is ExportQRScreenEvents.OnDecorationChange -> {
				_decoration.update { event.decoration }
				_isTooMuchEdit.update { false }
			}

			is ExportQRScreenEvents.OnQRTemplateChange ->
				_decoration.update { state -> state.swtichTemplate(event.template) }

			is ExportQRScreenEvents.OnExportMimeTypeChange -> _mimetype.update { event.mimeType }
			is ExportQRScreenEvents.OnExportDimensionChange -> _dimension.update { event.dimen }
			is ExportQRScreenEvents.OnExportBitmap -> validateAndExport(event.bitmap)
			ExportQRScreenEvents.OnCancelExport -> onCancelExport()
		}
	}

	private fun onPreviewSaveURI(uri: String) = viewModelScope.launch {
		_activityEvents.emit(LaunchActivityEvent.PreviewImageURI(uri))
	}

	private fun validateAndExport(bitmap: ImageBitmap) {
		_isExportRunning.update { true }

		// cancel the previous job and prepare a new one
		_exportJob?.cancel()
		_exportJob = viewModelScope.launch {

			val isValid = validateExport(bitmap)
			val isTooMuchEdit = _isTooMuchEdit.updateAndGet { !isValid }
			if (isTooMuchEdit) return@launch

			_uiEvents.emit(UIEvent.ShowToast("Verified"))

			val fileSaveFlow = fileFacade.saveImageContentToStorage(
				bytes = bitmap.toCompressedByteArray(),
				dimensions = _dimension.value,
				mimeType = _mimetype.value
			)

			fileSaveFlow.onEach { res ->
				when (res) {
					is Resource.Error -> {
						val message = res.message ?: res.error.message ?: "Cannot upload"
						_uiEvents.emit(UIEvent.ShowSnackBar(message))
					}

					is Resource.Success -> {
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
		// export finished
		_exportJob?.invokeOnCompletion {
			_isExportRunning.update { false }
		}
	}

	private fun onCancelExport() {
		_exportJob?.cancel()
		_exportJob = null
	}

	private suspend fun validateExport(bitmap: ImageBitmap): Boolean {
		val result = validator.isValid(bitmap.toRGBAModel())
		if (result.isFailure) {
			_uiEvents.emit(UIEvent.ShowSnackBar("Error In validating qr"))
		}
		return result.getOrNull() ?: false
	}
}