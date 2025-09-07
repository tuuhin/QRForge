package com.sam.qrforge.presentation.feature_export

import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_export.state.ExportDimensions
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExportQRViewModel : AppViewModel() {

	private val _decoration =
		MutableStateFlow<QRDecorationOption>(QRDecorationOption.QRDecorationOptionBasic())
	val selectedDecoration = _decoration.asStateFlow()

	private val _dimension = MutableStateFlow(ExportDimensions.Medium)
	val dimension = _dimension.asStateFlow()

	private val _isExportRunning = MutableStateFlow(false)
	val isExportRunning = _isExportRunning.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	fun onEvent(event: ExportQRScreenEvents) {
		when (event) {
			is ExportQRScreenEvents.OnDecorationChange -> _decoration.update { event.decoration }
			is ExportQRScreenEvents.OnQRTemplateChange ->
				_decoration.update { state -> state.switchTemplate(event.template) }

			is ExportQRScreenEvents.OnExportDimensionChange -> _dimension.update { event.dimen }
			is ExportQRScreenEvents.OnExportBitmap -> _uiEvents.tryEmit(UIEvent.ShowToast("REasy"))
		}
	}


	private fun QRDecorationOption.switchTemplate(template: QRTemplateOption): QRDecorationOption {
		return when (template) {
			QRTemplateOption.BASIC -> QRDecorationOption.QRDecorationOptionBasic(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
			)

			QRTemplateOption.MINIMALISTIC -> QRDecorationOption.QRDecorationOptionMinimal(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
			)

			QRTemplateOption.COLOR_LAYERED -> QRDecorationOption.QRDecorationOptionColorLayer(
				roundness = roundness,
				contentMargin = contentMargin,
				bitsSizeMultiplier = bitsSizeMultiplier,
				backGroundColor = backGroundColor,
				isDiamond = isDiamond,
			)
		}
	}
}