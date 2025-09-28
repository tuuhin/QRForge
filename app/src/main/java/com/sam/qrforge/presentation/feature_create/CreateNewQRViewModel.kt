package com.sam.qrforge.presentation.feature_create

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.data.mappers.toCompressedByteArray
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.CreateNewQRModel
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_create.state.CreateQREvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenEvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class CreateNewQRViewModel(
	private val generator: QRGeneratorFacade,
	private val contactsProvider: ContactsDataProvider,
	private val locationProvider: LocationProvider,
	private val repository: SavedQRDataRepository,
	private val saveGeneratedQRFacade: FileStorageFacade,
	private val analyticsLogger: AnalyticsTracker,
) : AppViewModel() {

	private val _saveQRState = MutableStateFlow(SaveQRScreenState())
	val saveQRState = _saveQRState.asStateFlow()

	private val _contentModel = MutableStateFlow<QRContentModel>(QRPlainTextModel(""))
	val qrContent = _contentModel.onStart { onGenerateQR() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(2000),
			initialValue = QRPlainTextModel("")
		)

	private val _generatedQR = MutableStateFlow<GeneratedQRModel?>(null)
	val generated = _generatedQR
		.map { qrModel -> qrModel?.toUIModel() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000L),
			initialValue = null
		)

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	private val _shareQREvent = MutableSharedFlow<LaunchActivityEvent>()
	val activityEvent = _shareQREvent.asSharedFlow()

	fun onCreateEvents(event: CreateQREvents) {
		when (event) {
			is CreateQREvents.OnQRDataTypeChange -> _contentModel.update { event.type.toNewModel() }
			is CreateQREvents.OnUpdateQRContent -> _contentModel.updateAndGet { event.content }
			is CreateQREvents.CheckContactsDetails -> findContactsFromURI(event.uri)
			CreateQREvents.CheckLastKnownLocation -> checkLastKnownLocation()
			is CreateQREvents.ShareGeneratedQR -> onShareGeneratedQR(event.bitmap)
			CreateQREvents.OnPreviewQR -> {
				analyticsLogger.logEvent(
					AnalyticsEvent.GENERATED_QR,
					mapOf(AnalyticsParams.GENERATED_QR_TYPE to _contentModel.value.type.toString())
				)
			}
		}
	}

	fun onSaveEvent(event: SaveQRScreenEvents) {
		when (event) {
			SaveQRScreenEvents.OnSave -> onSaveQR()
			is SaveQRScreenEvents.OnSaveQRDescChange -> _saveQRState.update { state ->
				state.copy(desc = event.textValue)
			}

			is SaveQRScreenEvents.OnSaveQRTitleChange -> _saveQRState.update { state ->
				state.copy(title = event.textValue, isError = false)
			}
		}
	}

	private fun onShareGeneratedQR(bitmap: ImageBitmap) = viewModelScope.launch {
		val bytes = bitmap.toCompressedByteArray()
		val fileResult = saveGeneratedQRFacade.saveContentToShare(bytes)
		fileResult.fold(
			onSuccess = { uriToShare ->
				_shareQREvent.emit(LaunchActivityEvent.ShareImageURI(uriToShare))
				analyticsLogger.logEvent(AnalyticsEvent.SHARE_GENERATED_QR)
			},
			onFailure = {
				val event = UIEvent.ShowToast("Failed to share")
				_uiEvents.emit(event)
			},
		)
	}

	private fun onSaveQR() {
		val screenState = _saveQRState.value
		val contentState = _contentModel.value
		val createModel = CreateNewQRModel(
			title = screenState.title.trim(),
			desc = screenState.desc.trim().ifBlank { null },
			content = contentState.toQRString(),
			format = contentState.type
		)

		if (createModel.title.isBlank() || !contentState.isValid) {
			_saveQRState.update { state -> state.copy(isError = true) }
			return
		}

		viewModelScope.launch {
			val result = repository.insertQRData(createModel)
			result.fold(
				onSuccess = { model ->
					analyticsLogger.logEvent(
						AnalyticsEvent.SAVE_QR,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to true,
							AnalyticsParams.QR_IS_SCANNED to false
						)
					)
					_uiEvents.emit(UIEvent.ShowToast("Saved!!"))
					_uiEvents.emit(UIEvent.NavigateBack)
				},
				onFailure = { err ->
					analyticsLogger.logEvent(
						AnalyticsEvent.SAVE_QR,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to false,
							AnalyticsParams.QR_IS_SCANNED to false,
							AnalyticsParams.ERROR_NAME to err::javaClass.name,
						)
					)
					val event = UIEvent.ShowSnackBar(err.message ?: "Unable to save")
					_uiEvents.emit(event)
				},
			)
		}
	}

	private fun checkLastKnownLocation() = viewModelScope.launch {
		val result = locationProvider.invoke()
		result.fold(
			onSuccess = { location ->
				_contentModel.update { content ->
					if (content !is QRGeoPointModel) content
					else content.copy(lat = location.latitude, long = location.longitude)
				}
			},
			onFailure = {
				val event = UIEvent.ShowSnackBar(it.message ?: "Location cannot be found")
				_uiEvents.emit(event)
			},
		)
	}

	private fun findContactsFromURI(uri: String) = viewModelScope.launch {
		val result = contactsProvider.invoke(uri)
		result.fold(
			onSuccess = { contacts ->
				val updatedContent = _contentModel.updateAndGet { content ->
					// update the content
					when (content) {
						is QRTelephoneModel -> content.copy(number = contacts.phoneNumber)
						is QRSmsModel -> content.copy(phoneNumber = contacts.phoneNumber)
						else -> content
					}
				}
				when (updatedContent) {
					is QRSmsModel -> _saveQRState.update { state ->
						state.copy(
							title = "SMS TO :${contacts.displayName} (${contacts.phoneNumber})",
							desc = "Message :${updatedContent.message}"
						)
					}

					is QRTelephoneModel -> _saveQRState.update { state ->
						state.copy(
							title = "Telephone Number (${contacts.displayName})",
							desc = "telephone number :${contacts.phoneNumber}"
						)
					}

					else -> {}
				}
			},
			onFailure = {
				val event = UIEvent.ShowSnackBar(it.message ?: "Cannot find Contacts data")
				_uiEvents.emit(event)
			},
		)
	}

	private fun onGenerateQR() = _contentModel.onEach { content ->
		if (!content.isValid) return@onEach
		val result = generator.generate(content)
		result.fold(
			onSuccess = { model -> _generatedQR.update { model } },
			onFailure = {
				val event = UIEvent.ShowSnackBar(it.message ?: "Unable to generate QR")
				_uiEvents.emit(event)
			},
		)
	}.launchIn(viewModelScope)

}