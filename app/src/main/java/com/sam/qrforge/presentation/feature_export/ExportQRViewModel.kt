package com.sam.qrforge.presentation.feature_export

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_create.util.toBytes
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExportQRViewModel(
	private val fileFacade: FileStorageFacade
) : AppViewModel() {

	private val _decoration =
		MutableStateFlow<QRDecorationOption>(QRDecorationOption.QRDecorationOptionBasic())
	val selectedDecoration = _decoration.asStateFlow()

	private val _dimension = MutableStateFlow(ExportDimensions.Medium)
	val dimension = _dimension.asStateFlow()

	private val _mimetype = MutableStateFlow(ImageMimeTypes.PNG)
	val mimeType = _mimetype.asStateFlow()

	private val _isExportRunning = MutableStateFlow(false)
	val isExportRunning = _isExportRunning.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	private val _activityEvents = MutableSharedFlow<LaunchActivityEvent>()
	val activityEvents = _activityEvents.asSharedFlow()

	fun onEvent(event: ExportQRScreenEvents) {
		when (event) {
			is ExportQRScreenEvents.OnDecorationChange -> _decoration.update { event.decoration }
			is ExportQRScreenEvents.OnQRTemplateChange ->
				_decoration.update { state -> state.swtichTemplate(event.template) }

			is ExportQRScreenEvents.OnExportMimeTypeChange -> _mimetype.update { event.mimeType }
			is ExportQRScreenEvents.OnExportDimensionChange -> _dimension.update { event.dimen }
			is ExportQRScreenEvents.OnExportBitmap -> beginExport(event.bitmap)
		}
	}

	private fun beginExport(bitmap: ImageBitmap) = viewModelScope.launch {
		val bytes = bitmap.toBytes()

		val fileFlow = fileFacade.saveImageContentToStorage(
			bytes = bytes,
			dimensions = _dimension.value,
			mimeType = _mimetype.value
		)

		fileFlow.onEach { res ->
			when (res) {
				is Resource.Error -> {
					val message = res.message ?: res.error.message ?: "Cannot upload"
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				is Resource.Success -> {
					val event = UIEvent.ShowSnackBarWithAction(
						message = "Successfully Exported",
						actionText = "Preview",
						action = { onPreviewSaveURI(res.data) })
					_uiEvents.emit(event)
				}

				else -> {}
			}
			_isExportRunning.update { res is Resource.Loading }
		}.launchIn(this)
	}

	private fun onPreviewSaveURI(uri: String) = viewModelScope.launch {
		_activityEvents.emit(LaunchActivityEvent.PreviewImageURI(uri))
	}

}