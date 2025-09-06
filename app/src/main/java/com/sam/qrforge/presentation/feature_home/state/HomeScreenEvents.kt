package com.sam.qrforge.presentation.feature_home.state

import com.sam.qrforge.domain.models.SavedQRModel

sealed interface HomeScreenEvents {

	data class OnGenerateQR(val model: SavedQRModel) : HomeScreenEvents
	data class OnDeleteItem(val model: SavedQRModel) : HomeScreenEvents

	data class OnListFilterChange(val filter: FilterQRListState) : HomeScreenEvents
}