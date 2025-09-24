package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_home.state.FilterQRListState
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.SortOption
import com.sam.qrforge.presentation.feature_home.state.SortOrder
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
private fun FilterBottomSheetContent(
	onSortOrderChange: (SortOrder) -> Unit,
	onSortOptionChange: (SortOption) -> Unit,
	onShowOnlyFavChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	showOnlyFav: Boolean = false,
	hasItems: Boolean = true,
	selectedOrder: SortOrder = SortOrder.ASC,
	selectedOption: SortOption = SortOption.CREATED,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	titleTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
	Column(
		modifier = modifier
			.heightIn(64.dp)
			.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Spacer(modifier = Modifier.height(4.dp))
		if (!hasItems) {
			Text(
				text = stringResource(R.string.filter_options_sheet_no_items),
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.align(Alignment.CenterHorizontally)
			)
			return
		}
		Text(
			text = stringResource(R.string.filter_options_title_sort_options),
			style = titleTextStyle
		)
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.fillMaxWidth()
		) {
			SortOption.entries.forEach { option ->
				FilterChip(
					selected = option == selectedOption,
					onClick = { onSortOptionChange(option) },
					label = { Text(text = option.stringRes) },
					shape = MaterialTheme.shapes.large,
				)
			}
		}
		Text(
			text = stringResource(R.string.filter_options_title_sort_order),
			style = titleTextStyle
		)
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.fillMaxWidth()
		) {
			SortOrder.entries.forEach { order ->
				FilterChip(
					selected = order == selectedOrder,
					onClick = { onSortOrderChange(order) },
					label = {
						Text(
							text = order.stringRes,
							style = MaterialTheme.typography.labelLarge
						)
					},
					shape = MaterialTheme.shapes.large,
				)
			}
		}
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = stringResource(R.string.filter_options_title_only_fav),
				style = titleTextStyle,
				modifier = Modifier.weight(1f)
			)
			Switch(
				checked = showOnlyFav,
				onCheckedChange = onShowOnlyFavChange,
				colors = SwitchDefaults.colors(
					checkedTrackColor = MaterialTheme.colorScheme.secondary,
					checkedThumbColor = MaterialTheme.colorScheme.onSecondary
				)
			)
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterListBottomSheet(
	showSheet: Boolean,
	onDismissSheet: () -> Unit,
	onEvent: (HomeScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	hasItems: Boolean = true,
	sheetState: SheetState = rememberModalBottomSheetState(),
	filterState: FilterQRListState = FilterQRListState(),
) {
	if (!showSheet) return

	ModalBottomSheet(
		onDismissRequest = onDismissSheet,
		sheetState = sheetState,
		modifier = modifier,
	) {
		FilterBottomSheetContent(
			selectedOrder = filterState.sortOrder,
			selectedOption = filterState.sortOption,
			showOnlyFav = filterState.showOnlyFav,
			hasItems = hasItems,
			onShowOnlyFavChange = {
				val newState = filterState.copy(showOnlyFav = it)
				onEvent(HomeScreenEvents.OnListFilterChange(newState))
			},
			onSortOrderChange = {
				val newState = filterState.copy(sortOrder = it)
				onEvent(HomeScreenEvents.OnListFilterChange(newState))
			},
			onSortOptionChange = {
				val newState = filterState.copy(sortOption = it)
				onEvent(HomeScreenEvents.OnListFilterChange(newState))
			},
			contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.bottom_sheet_content_padding))
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun FilterBottomSheetContentPreview() = QRForgeTheme {
	Surface(
		color = BottomSheetDefaults.ContainerColor,
		shape = BottomSheetDefaults.ExpandedShape
	) {
		FilterBottomSheetContent(
			onSortOptionChange = {},
			onSortOrderChange = {},
			onShowOnlyFavChange = {},
			contentPadding = PaddingValues(dimensionResource(R.dimen.bottom_sheet_content_padding))
		)
	}
}