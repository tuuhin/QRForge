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
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun QRModelList(
	generatedQR: ImmutableList<SavedAndGeneratedQRModel>,
	onDeleteItem: (SavedQRModel) -> Unit,
	modifier: Modifier = Modifier,
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
		modifier = modifier,
	) {
		itemsIndexed(
			items = generatedQR,
			key = itemsKeys,
			contentType = itemsContentType
		) { idx, item ->
			QRModelCard(
				selectableModel = item,
				onDeleteItem = { onDeleteItem(item.qrModel) },
				modifier = Modifier
					.fillMaxWidth()
					.animateItem(),
			)
		}
	}
}