package com.sam.qrforge.presentation.feature_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.GeneratedQRModel
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenEvents
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenState
import com.sam.qrforge.presentation.navigation.nav_graph.NavRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class QRDetailsViewModel(
	private val generator: QRGeneratorFacade,
	private val repository: SavedQRDataRepository,
	private val savedStateHandle: SavedStateHandle,
) : AppViewModel() {

	private val route: NavRoutes.QRDetailsScreen
		get() = savedStateHandle.toRoute()

	private val _savedModel = MutableStateFlow<SavedQRModel?>(null)
	private val _generatedQR = MutableStateFlow<GeneratedQRModel?>(null)
	private val _isLoading = MutableStateFlow(true)

	val screenState = combine(
		_savedModel, _isLoading, _generatedQR
	) { qrModel, isLoading, generated ->
		QRDetailsScreenState(
			qrModel = qrModel,
			isLoading = isLoading,
			generatedModel = generated?.toUIModel()
		)
	}.onStart {
		loadContent()
		onGenerateQR()
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000L),
		initialValue = QRDetailsScreenState()
	)

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents

	fun onEvent(event: QRDetailsScreenEvents) {
		when (event) {
			QRDetailsScreenEvents.OnShareQR -> {}
			is QRDetailsScreenEvents.ToggleIsFavourite -> {}
		}
	}

	private fun loadContent() = repository.fetchQRById(route.qrId)
		.onEach { res ->
			when (res) {
				is Resource.Error -> {
					val message = res.message ?: res.error.message ?: "Unable to load"
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				is Resource.Success -> _savedModel.update { res.data }
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