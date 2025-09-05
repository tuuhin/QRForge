package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun QRModelList(
	generatedQR: ImmutableList<SavedAndGeneratedQRModel>,
	modifier: Modifier = Modifier,
	onSelectItem: (SavedQRModel) -> Unit = {},
	onDeleteItem: (SavedQRModel) -> Unit = {},
	onGenerateQR: (SavedQRModel) -> Unit = {},
	state: LazyListState = rememberLazyListState(),
	contentPadding: PaddingValues = PaddingValues(),
) {

	val isInspectionMode = LocalInspectionMode.current

	val itemsKeys: ((Int, SavedAndGeneratedQRModel) -> Any)? = remember {
		if (isInspectionMode) null else { _, model -> model.qrModel.id }
	}

	val itemsContentType: (Int, SavedAndGeneratedQRModel) -> Any? = remember {
		{ _, model -> model::class.simpleName }
	}

	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(6.dp),
		contentPadding = contentPadding,
		state = state,
		modifier = modifier,
	) {
		itemsIndexed(
			items = generatedQR,
			key = itemsKeys,
			contentType = itemsContentType
		) { idx, item ->
			QRModelCard(
				model = item,
				onDeleteItem = { onDeleteItem(item.qrModel) },
				onSelectItem = { onSelectItem(item.qrModel) },
				onGenerateQR = { onGenerateQR(item.qrModel) },
				modifier = Modifier
					.fillMaxWidth()
					.animateItem()
					.sharedBoundsWrapper(SharedTransitionKeys.sharedBoundsToItemDetail(item.qrModel.id)),
			)
		}
	}
}