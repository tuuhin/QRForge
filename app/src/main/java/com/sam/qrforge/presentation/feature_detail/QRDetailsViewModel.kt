package com.sam.qrforge.presentation.feature_detail

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sam.qrforge.data.mappers.toCompressedByteArray
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import com.sam.qrforge.domain.provider.WIFIConnectionProvider
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.repository.exception.CannotFindMatchingIdException
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_detail.state.DeleteQRDialogState
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenEvent
import com.sam.qrforge.presentation.feature_detail.state.EditQRScreenState
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenEvents
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenState
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class QRDetailsViewModel(
	private val generator: QRGeneratorFacade,
	private val wifiConnector: WIFIConnectionProvider,
	private val repository: SavedQRDataRepository,
	private val savedStateHandle: SavedStateHandle,
	private val fileFacade: FileStorageFacade,
	private val analyticsLogger: AnalyticsTracker,
) : AppViewModel() {

	private val route: NavRoutes.QRDetailsRoute
		get() = savedStateHandle.toRoute()

	private val _savedModel = MutableStateFlow<SavedQRModel?>(null)
	private val _generatedQR = MutableStateFlow<GeneratedQRModel?>(null)
	private val _isLoading = MutableStateFlow(true)
	private val _deleteDialogState = MutableStateFlow(DeleteQRDialogState())

	val screenState = combine(
		_savedModel,
		_isLoading,
		_generatedQR,
		_deleteDialogState
	) { qrModel, isLoading, generated, dialogState ->
		QRDetailsScreenState(
			qrModel = qrModel,
			isLoading = isLoading,
			generatedModel = generated?.toUIModel(),
			showDeleteDialog = dialogState.showDialog,
			deleteEnabled = dialogState.canDeleteItem
		)
	}.onStart {
		loadContent()
		onGenerateQR()
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000L),
		initialValue = QRDetailsScreenState()
	)

	private val _editState = MutableStateFlow(EditQRScreenState())
	val editState = _editState.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	private val _activityEvents = MutableSharedFlow<LaunchActivityEvent>()
	val activityEvents = _activityEvents.asSharedFlow()

	fun onEvent(event: QRDetailsScreenEvents) {
		when (event) {
			is QRDetailsScreenEvents.OnShareQR -> onShareGeneratedQR(event.bitmap)
			is QRDetailsScreenEvents.ToggleIsFavourite -> toggleIsFavourite(event.model)
			QRDetailsScreenEvents.ToggleDeleteDialog -> _deleteDialogState.update { state ->
				state.copy(showDialog = !state.showDialog)
			}

			QRDetailsScreenEvents.DeleteCurrentQR -> onDeleteQR()
			QRDetailsScreenEvents.ActionConnectToWifi -> connectToWifiNetwork()
		}
	}

	fun onEditEvent(event: EditQRScreenEvent) {
		when (event) {
			is EditQRScreenEvent.OnSaveQRDescChange -> _editState.update { state ->
				state.copy(desc = event.textValue)
			}

			is EditQRScreenEvent.OnSaveQRTitleChange -> _editState.update { state ->
				state.copy(title = event.textValue)
			}

			EditQRScreenEvent.OnEdit -> onUpdateContent()
		}
	}

	private fun connectToWifiNetwork() {
		val model = (_savedModel.value?.content as? QRWiFiModel) ?: return
		val ssid = model.ssid ?: return

		val connectionFlow = wifiConnector.connectToWifi(
			ssid = ssid,
			passphrase = model.password,
			isHidden = model.isHidden
		)

		connectionFlow.onEach { res ->
			when (res) {
				Resource.Loading -> _uiEvents.emit(UIEvent.ShowToast("Connecting"))
				is Resource.Error -> {
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_CONTEXT_ACTION_FAILED,
						mapOf(AnalyticsParams.GENERATED_QR_TYPE to QRDataType.TYPE_WIFI)
					)
					val message = res.message ?: res.error.message ?: "Cannot connect "
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				is Resource.Success -> {
					val message = if (res.data) "Connected" else "Denied"
					_uiEvents.emit(UIEvent.ShowToast(message))
				}
			}
		}.launchIn(viewModelScope)
	}

	private fun onShareGeneratedQR(bitmap: ImageBitmap) = viewModelScope.launch {
		val bytes = bitmap.toCompressedByteArray()
		val fileResult = fileFacade.saveContentToShare(bytes)
		fileResult.fold(
			onSuccess = { uriToShare ->
				analyticsLogger.logEvent(AnalyticsEvent.SHARE_GENERATED_QR)
				_uiEvents.emit(UIEvent.ShowToast("Sharing QR"))
				_activityEvents.emit(LaunchActivityEvent.ShareImageURI(uriToShare))
			},
			onFailure = {
				val event = UIEvent.ShowToast("Failed to share")
				_uiEvents.emit(event)
			},
		)
	}

	private fun onDeleteQR() {
		val model = _savedModel.value ?: return
		_deleteDialogState.update { state -> state.copy(canDeleteItem = false) }

		viewModelScope.launch {
			val result = repository.deleteQRModel(model)
			result.fold(
				onSuccess = {
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_DELETE,
						mapOf(AnalyticsParams.IS_SUCCESSFUL to true)
					)
					_uiEvents.emit(UIEvent.ShowToast("Item Deleted"))
					_uiEvents.emit(UIEvent.NavigateBack)
				},
				onFailure = { err ->
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_DELETE,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to false,
							AnalyticsParams.ERROR_NAME to err::javaClass.name
						)
					)
					val event = UIEvent.ShowToast(err.message ?: "Error")
					_uiEvents.emit(event)
				},
			)
		}.invokeOnCompletion {
			// on completion
			_deleteDialogState.update { state ->
				state.copy(canDeleteItem = true, showDialog = false)
			}
		}
	}

	private fun onUpdateContent() {
		val currentQRModel = _savedModel.value ?: return
		val fieldsState = _editState.value
		// check if title is not blank
		if (fieldsState.title.isBlank()) {
			_editState.update { state -> state.copy(isError = true) }
			return
		}
		// check if the content is same
		if (fieldsState.title == currentQRModel.title && fieldsState.desc == currentQRModel.desc) {
			viewModelScope.launch { _uiEvents.emit(UIEvent.ShowToast("Content Same")) }
			return
		}
		// update the model
		viewModelScope.launch {
			val qRModel = currentQRModel.copy(
				title = fieldsState.title.trim(),
				desc = fieldsState.desc.trim().ifBlank { null },
			)

			val result = repository.updateQRModel(qRModel)
			result.fold(
				onSuccess = {
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_METADATA_UPDATE,
						mapOf(AnalyticsParams.IS_SUCCESSFUL to true)
					)
					_uiEvents.emit(UIEvent.ShowToast("Content Updated"))
					_uiEvents.emit(UIEvent.NavigateBack)
				},
				onFailure = { err ->
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_METADATA_UPDATE,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to false,
							AnalyticsParams.ERROR_NAME to err.javaClass.simpleName
						)
					)
					val event = UIEvent.ShowSnackBar(err.message ?: "Unable to save")
					_uiEvents.emit(event)
				},
			)
		}
	}

	private fun toggleIsFavourite(model: SavedQRModel) = viewModelScope.launch {
		val result = repository.toggleFavourite(model)
		result.fold(
			onSuccess = { model ->
				val message = if (model.isFav) "Marked as favourite" else "Removed from favourites"
				val event = UIEvent.ShowSnackBar(message)
				_uiEvents.emit(event)
			},
			onFailure = { err ->
				analyticsLogger.logEvent(
					AnalyticsEvent.QR_METADATA_UPDATE,
					mapOf(
						AnalyticsParams.IS_SUCCESSFUL to false,
						AnalyticsParams.ERROR_NAME to err.javaClass.simpleName
					)
				)
				val event = UIEvent.ShowSnackBar(err.message ?: "Error")
				_uiEvents.emit(event)
			},
		)
	}

	private fun loadContent() = repository.fetchQRById(route.qrId)
		.onEach { res ->
			when (res) {
				is Resource.Error -> {
					val isMissingId = res.error is CannotFindMatchingIdException
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_DETAILS_LOAD,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to false,
							AnalyticsParams.ERROR_NAME to res.error.javaClass.simpleName,
							AnalyticsParams.ERROR_WRONG_ID to isMissingId
						)
					)
					val message = res.message ?: res.error.message ?: "Unable to load"
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				is Resource.Success -> {
					analyticsLogger.logEvent(
						AnalyticsEvent.QR_DETAILS_LOAD,
						mapOf(AnalyticsParams.IS_SUCCESSFUL to true)
					)
					val model = _savedModel.updateAndGet { res.data }
					_editState.update { state ->
						state.copy(title = model?.title ?: "", desc = model?.desc ?: "")
					}
				}

				else -> {}
			}
			_isLoading.update { res is Resource.Loading }
		}.launchIn(viewModelScope)


	private fun onGenerateQR() = _savedModel.filterNotNull()
		.map { it.content }
		.distinctUntilChanged()
		.onEach { qrContent ->
			val result = generator.generate(qrContent)
			result.fold(
				onSuccess = { model -> _generatedQR.update { model } },
				onFailure = {
					val event = UIEvent.ShowSnackBar(it.message ?: "Unable to generate QR")
					_uiEvents.emit(event)
				},
			)
		}.launchIn(viewModelScope)
}