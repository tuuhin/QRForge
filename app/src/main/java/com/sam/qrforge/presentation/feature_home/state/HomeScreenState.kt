package com.sam.qrforge.presentation.feature_home.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class HomeScreenState(
	private val savedQRList: ImmutableList<SavedAndGeneratedQRModel> = persistentListOf(),
	val filterState: FilterQRListState = FilterQRListState(),
	val isContentLoaded: Boolean = false,
) {

	val filteredQRList: ImmutableList<SavedAndGeneratedQRModel>
		get() = savedQRList.filteredResults(filterState).toImmutableList()

	val showFilterOption: Boolean
		get() = when (filterState.selectedType) {
			null -> savedQRList.isNotEmpty()
			else -> savedQRList.any { it.qrModel.format == filterState.selectedType }
		}

	val hasFavouriteItem: Boolean
		get() = filteredQRList.any { it.qrModel.isFav }
}
