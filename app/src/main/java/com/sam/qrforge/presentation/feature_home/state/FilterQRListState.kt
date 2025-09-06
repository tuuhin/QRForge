package com.sam.qrforge.presentation.feature_home.state

import com.sam.qrforge.domain.enums.QRDataType

data class FilterQRListState(
	val selectedType: QRDataType? = null,
	val sortOption: SortOption = SortOption.CREATED,
	val sortOrder: SortOrder = SortOrder.DESC,
	val showOnlyFav: Boolean = false,
)
