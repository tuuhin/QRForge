package com.sam.qrforge.presentation.feature_home

import androidx.lifecycle.viewModelScope
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.domain.facade.QRGeneratorFacade
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.domain.repository.SavedQRDataRepository
import com.sam.qrforge.domain.util.Resource
import com.sam.qrforge.presentation.common.mappers.toUIModel
import com.sam.qrforge.presentation.common.utils.AppViewModel
import com.sam.qrforge.presentation.common.utils.UIEvent
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
	private val repository: SavedQRDataRepository,
	private val generator: QRGeneratorFacade
) : AppViewModel() {

	private val _selectedTypeFilter = MutableStateFlow<QRDataType?>(null)
	val typeFilter = _selectedTypeFilter.asStateFlow()

	private val _savedQR = MutableStateFlow<List<SavedAndGeneratedQRModel>>(emptyList())

	private val _isLoading = MutableStateFlow(true)
	val isLoading = _isLoading.asStateFlow()

	val savedQR = combine(_savedQR, _selectedTypeFilter) { savedQR, filter ->
		if (filter == null) savedQR
		else savedQR.filter { it.qrModel.format == filter }
	}
		.onStart { loadQR() }
		.map { models -> models.toImmutableList() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(2_000L),
			initialValue = persistentListOf()
		)

	private val _uiEvents = MutableSharedFlow<UIEvent>()
	override val uiEvents: SharedFlow<UIEvent>
		get() = _uiEvents


	fun onEvent(event: HomeScreenEvents) {
		when (event) {
			is HomeScreenEvents.OnFilterQRDataType -> _selectedTypeFilter.update { event.type }
			is HomeScreenEvents.OnDeleteItem -> onDeleteItem(event.model)
		}
	}

	private fun loadQR() = repository.fetchAllSavedQR()
		.onEach { res ->
			when (res) {
				is Resource.Error -> {
					_isLoading.update { false }
					val message = res.message ?: res.error.message ?: "Unable to load"
					_uiEvents.emit(UIEvent.ShowSnackBar(message))
				}

				Resource.Loading -> _isLoading.update { true }
				is Resource.Success -> {
					_isLoading.update { false }
					onGenerateQR(res.data)
				}
			}
		}.launchIn(viewModelScope)

	private fun onDeleteItem(model: SavedQRModel) = viewModelScope.launch {
		val result = repository.deleteQRModel(model)
		result.fold(
			onFailure = { err ->
				val event = UIEvent.ShowSnackBar(err.message ?: "Unable to delete item")
				_uiEvents.emit(event)
			},
			onSuccess = {
				_uiEvents.emit(UIEvent.ShowToast("Deleted Item "))
			},
		)
	}

	private fun onGenerateQR(models: List<SavedQRModel>) = viewModelScope.launch {
		// filter the new ones
		val models = models.map {
			async {
				val generator = generator.generate(it.content)
				val uiModel = if (generator.isSuccess) generator.getOrThrow().toUIModel()
				else null
				SavedAndGeneratedQRModel(qrModel = it, uiModel)
			}
		}
		val results = models.awaitAll()
		_savedQR.update { results }
	}
}