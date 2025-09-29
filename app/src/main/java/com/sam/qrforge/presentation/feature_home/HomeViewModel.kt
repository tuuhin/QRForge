package com.sam.qrforge.presentation.feature_home

import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.analytics.AnalyticsEvent
import com.sam.qrforge.domain.analytics.AnalyticsParams
import com.sam.qrforge.domain.analytics.AnalyticsTracker
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_home.state.FilterQRListState
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.HomeScreenState
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
	private val repository: SavedQRDataRepository,
	private val generator: QRGeneratorFacade,
	private val analyticsLogger: AnalyticsTracker
) : AppViewModel() {

	private val _filterState = MutableStateFlow(FilterQRListState())
	private val _savedQR = MutableStateFlow<List<SavedAndGeneratedQRModel>>(emptyList())
	private val _isLoading = MutableStateFlow(true)

	val homeScreenState = combine(
		_savedQR, _filterState, _isLoading
	) { savedQR, filter, isLoading ->
		HomeScreenState(
			savedQRList = savedQR.toImmutableList(),
			isContentLoaded = !isLoading,

			filterState = filter
		)
	}.onStart { loadQR() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(2_000L),
			initialValue = HomeScreenState()
		)

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents


	fun onEvent(event: HomeScreenEvents) {
		when (event) {
			is HomeScreenEvents.OnDeleteItem -> onDeleteItem(event.model)
			is HomeScreenEvents.OnGenerateQR -> onGenerateQR(event.model)
			is HomeScreenEvents.OnListFilterChange -> _filterState.update { state -> event.filter }
		}
	}

	private fun loadQR() = repository.fetchAllSavedQR()
		.onEach { res ->
			when (res) {
				is Resource.Error -> {
					analyticsLogger.logEvent(
						AnalyticsEvent.LOAD_ALL_QR,
						mapOf(
							AnalyticsParams.IS_SUCCESSFUL to false,
							AnalyticsParams.ERROR_NAME to res.error.javaClass::getSimpleName
						)
					)
					val message = res.message ?: res.error.message ?: "Unable to load"
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				is Resource.Success -> onUpdateModels(res.data)
				else -> {}
			}
			_isLoading.update { res is Resource.Loading }
		}.launchIn(viewModelScope)

	private fun onUndoItemDelete(model: SavedQRModel) = viewModelScope.launch {
		val result = repository.updateQRModel(model)
		result.onFailure { err ->
			val event = UIEvent.ShowSnackBar(err.message ?: "Cannot add item")
			_uiEvents.emit(event)
		}
	}

	private fun onDeleteItem(model: SavedQRModel) = viewModelScope.launch {
		val fallback = model
		val result = repository.deleteQRModel(model)
		result.fold(
			onFailure = { err ->
				val event = UIEvent.ShowSnackBar(err.message ?: "Unable to delete item")
				_uiEvents.emit(event)
			},
			onSuccess = {
				val event = UIEvent.ShowSnackBarWithAction(
					message = "QR code removed",
					actionText = "UNDO",
					action = { onUndoItemDelete(fallback) })
				_uiEvents.emit(event)
			},
		)
	}

	private fun onUpdateModels(models: List<SavedQRModel>) {
		// previous is acting like a cache
		val savedAndGeneratedQr = _savedQR.value
		val previousSavedQR = _savedQR.value.map { it.qrModel }

		val updated = models.map { newModel ->
			// if the new model is not in previous no create an empty instance
			if (newModel !in previousSavedQR) return@map SavedAndGeneratedQRModel(newModel)
			// if new model is present use the earlier one
			savedAndGeneratedQr.find { it.qrModel.id == newModel.id }
				?: SavedAndGeneratedQRModel(newModel)
		}
		// normal update
		_savedQR.update { updated }
	}

	private fun onGenerateQR(model: SavedQRModel) {
		// if id and content is same
		val savedAndGeneratedModel = _savedQR.value
			.find { it.qrModel.id == model.id && it.qrModel.content == model.content }

		// ui model is already set and the content not changed so need to build the model once again
		if (savedAndGeneratedModel?.uiModel != null) return

		viewModelScope.launch {
			val generator = generator.generate(model.content)

			// it's a failure so no need to update the model
			if (generator.isFailure) return@launch

			val newResult = SavedAndGeneratedQRModel(
				qrModel = model,
				uiModel = generator.getOrNull()?.toUIModel()
			)

			// find the one asked for and update it
			_savedQR.update { models ->
				models.map { oldModel ->
					if (oldModel.qrModel.id == model.id) newResult
					else oldModel
				}
			}
		}
	}
}