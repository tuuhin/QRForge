package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.presentation.common.composables.painter
import com.sam.qrforge.presentation.common.composables.string

@Composable
fun QRDataTypePickerRow(
	onSelectType: (QRDataType?) -> Unit,
	modifier: Modifier = Modifier,
	selectedType: QRDataType? = null,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	shape: Shape = MaterialTheme.shapes.large,
) {
	LazyRow(
		modifier = modifier,
		contentPadding = contentPadding,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		item {
			InputChip(
				selected = selectedType == null,
				onClick = { onSelectType(null) },
				label = { Text(text = "All") },
				shape = shape
			)
		}
		itemsIndexed(
			items = QRDataType.entries,
			key = { _, type -> type.name },
		) { _, item ->
			InputChip(
				selected = item == selectedType,
				onClick = { onSelectType(item) },
				label = { Text(text = item.string) },
				leadingIcon = {
					Icon(
						painter = item.painter,
						contentDescription = "Icon for :$item",
						modifier = Modifier.size(20.dp)
					)
				},
				shape = shape
			)
		}
	}
}