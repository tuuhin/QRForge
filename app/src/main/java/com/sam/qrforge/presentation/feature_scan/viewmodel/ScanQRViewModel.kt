package com.sam.qrforge.presentation.feature_scan.viewmodel

import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ScanQRViewModel(
	private val generator: QRGeneratorFacade
) : AppViewModel() {

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents.asSharedFlow()

	fun onGenerate(model: QRContentModel) = viewModelScope.launch {
		_uiEvents.emit(UIEvent.ShowToast("Found qr model"))
	}
}