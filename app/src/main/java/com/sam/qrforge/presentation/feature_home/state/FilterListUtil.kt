package com.sam.qrforge.presentation.feature_home.state

private fun <T, R> Iterable<T>.sortSelector(
	sortOrder: SortOrder,
	selector: (T) -> Comparable<R>
): List<T> {
	return sortedWith { a, b ->
		when (sortOrder) {
			SortOrder.ASC -> compareValuesBy(a, b, selector)
			SortOrder.DESC -> compareValuesBy(b, a, selector)
		}
	}
}

internal fun List<SavedAndGeneratedQRModel>.filteredResults(sortInfo: FilterQRListState): List<SavedAndGeneratedQRModel> {
	val typeFilerResult = if (sortInfo.selectedType == null) this
	else filter { it.qrModel.format == sortInfo.selectedType }
	val sorted = when (sortInfo.sortOption) {
		SortOption.CREATED -> typeFilerResult.sortSelector(sortInfo.sortOrder) { it.qrModel.createdAt }
		SortOption.NAME -> typeFilerResult.sortSelector(sortInfo.sortOrder) { it.qrModel.title }
	}
	return if (sortInfo.showOnlyFav) sorted.filter { it.qrModel.isFav } else sorted
}