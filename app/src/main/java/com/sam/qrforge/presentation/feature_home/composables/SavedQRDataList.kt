package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.feature_home.state.SelectableQRModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SavedQRDataList(
	qrModels: ImmutableList<SavedQRModel>,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	spacing: Dp = 12.dp,
) {

	val isInspectionMode = LocalInspectionMode.current

	val itemsKeys: ((Int, SavedQRModel) -> Any)? = remember {
		if (isInspectionMode) null else { _, model -> model.id }
	}

	val itemsContentType: (Int, SavedQRModel) -> Any? = remember {
		{ _, model -> model::class.simpleName }
	}

	LazyColumn(
		modifier = modifier,
		contentPadding = contentPadding,
		verticalArrangement = Arrangement.spacedBy(spacing),
	) {
		itemsIndexed(
			items = qrModels,
			key = itemsKeys,
			contentType = itemsContentType
		) { idx, item ->
			SelectableQRModelCard(
				selectableModel = SelectableQRModel(item, isSelected = false),
				modifier = Modifier
					.fillMaxWidth()
					.animateItem(),
			)
		}
	}
}