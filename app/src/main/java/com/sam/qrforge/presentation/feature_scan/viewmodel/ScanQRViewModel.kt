package com.sam.qrforge.presentation.feature_scan.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.facade.FileStorageFacade
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import com.sam.qrforge.domain.provider.WIFIConnectionProvider
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.LaunchActivityEvent
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.common.utils.toBytes
import com.sam.qrforge.presentation.feature_scan.state.ScanResultScreenEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanQRViewModel(
	private val generator: QRGeneratorFacade,
	private val wifiConnector: WIFIConnectionProvider,
	private val fileFacade: FileStorageFacade,
) : AppViewModel() {

	private val _generatedModel = MutableStateFlow<GeneratedQRUIModel?>(null)
	val generated = _generatedModel
		.onStart { onUpdateGeneratedModel() }
		.stateIn(viewModelScope, SharingStarted.Lazily, null)

	private val _qrContentModel = MutableStateFlow<QRContentModel?>(null)
	val qrContent = _qrContentModel.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents.asSharedFlow()

	private val _activityEvents = MutableSharedFlow<LaunchActivityEvent>()
	val activityEvents = _activityEvents.asSharedFlow()

	fun onEvent(event: ScanResultScreenEvents) {
		when (event) {
			ScanResultScreenEvents.ConnectToWifi -> connectToWifiNetwork()
			is ScanResultScreenEvents.ShareScannedResults -> onShareGeneratedQR(event.bitmap)
			is ScanResultScreenEvents.GenerateQR -> _qrContentModel.update { event.content }
		}
	}

	private fun onShareGeneratedQR(bitmap: ImageBitmap) = viewModelScope.launch {
		val bytes = bitmap.toBytes()
		val fileResult = fileFacade.saveContentToShare(bytes)
		fileResult.fold(
			onSuccess = { uriToShare ->
				_uiEvents.emit(UIEvent.ShowToast("Sharing QR"))
				_activityEvents.emit(LaunchActivityEvent.ShareImageURI(uriToShare))
			},
			onFailure = {
				val event = UIEvent.ShowToast("Failed to share")
				_uiEvents.emit(event)
			},
		)
	}

	private fun connectToWifiNetwork() {
		val model = (qrContent.value as? QRWiFiModel) ?: return
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


	private fun onUpdateGeneratedModel() = _qrContentModel.filterNotNull()
		.onEach { model ->
			val result = generator.generate(model, useHints = true)
			result.fold(
				onSuccess = { model -> _generatedModel.update { model.toUIModel() } },
				onFailure = {
					_uiEvents.emit(UIEvent.ShowSnackBar("Cannot Generate QR"))
				},
			)
		}.launchIn(viewModelScope)
}
