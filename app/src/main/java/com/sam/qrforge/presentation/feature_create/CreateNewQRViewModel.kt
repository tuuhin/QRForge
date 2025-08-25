package com.sam.qrforge.presentation.feature_create

import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.provider.ContactsDataProvider
import com.sam.qrforge.domain.provider.LocationProvider
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_create.state.CreateQREvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenEvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
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

	fun onCreateEvents(event: CreateQREvents) {
		when (event) {
			is CreateQREvents.OnQRDataTypeChange -> {
				val model = event.type.toNewModel()
				_contentModel.update { model }
			}

			is CreateQREvents.OnUpdateQRContent -> onQRContentChange(event.content)
			is CreateQREvents.CheckContactsDetails -> findContactsFromURI(event.uri)
			CreateQREvents.CheckLastKnownLocation -> checkLastKnownLocation()
		}
	}

	fun onSaveEvent(event: SaveQRScreenEvents) {
		when (event) {
			is SaveQRScreenEvents.OnSaveQRDescChange -> _saveQRState.update { state ->
				state.copy(desc = event.textValue)
			}

			is SaveQRScreenEvents.OnSaveQRTitleChange -> _saveQRState.update { state ->
				state.copy(title = event.textValue)
			}

			SaveQRScreenEvents.OnSave -> onSaveQR()
		}
	}

	private fun onQRContentChange(content: QRContentModel) {
		val newContent = _contentModel.updateAndGet { content }
		// update the screen state

	}

	private fun onSaveQR() {
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
				_contentModel.update { content ->
					when (content) {
						is QRTelephoneModel -> content.copy(number = contacts.phoneNumber)
						is QRSmsModel -> content.copy(phoneNumber = contacts.phoneNumber)
						else -> content
					}
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