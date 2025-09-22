package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.feature_export.composable.edit_blocks.EditDecorationBooleanOptions
import com.sam.qrforge.presentation.feature_export.composable.edit_blocks.EditDecorationSliderOptions
import com.sam.qrforge.presentation.feature_export.composable.edit_blocks.QREditBlockSelectColorLayer
import com.sam.qrforge.presentation.feature_export.composable.edit_blocks.QREditDecorationColorOptions
import com.sam.qrforge.ui.theme.QRForgeTheme

fun LazyListScope.editQRDecorationOptions(
	onDecorationChange: (QRDecorationOption) -> Unit,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
) {
	item(key = LazyContentKeys.OPTION_QR_EDIT_SLIDERS) {

		val initialValue = remember { QRDecorationOption.QRDecorationOptionBasic() }

		EditDecorationSliderOptions(
			initialValue = initialValue,
			onMarginChange = { margin ->
				val modified = decoration.copyProperties(contentMargin = margin)
				onDecorationChange(modified)
			},
			onRoundnessChange = { roundness ->
				val modified = decoration.copyProperties(roundness = roundness)
				onDecorationChange(modified)
			},
			onBitsMultiplierChange = { bitSize ->
				val modified = decoration.copyProperties(bitsSizeMultiplier = bitSize)
				onDecorationChange(modified)
			},
			contentPadding = PaddingValues(all = dimensionResource(R.dimen.qr_edit_option_internal_padding)),
			shape = MaterialTheme.shapes.large.copy(
				bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
				bottomEnd = MaterialTheme.shapes.extraSmall.bottomEnd
			)
		)
	}
	if (decoration is QRDecorationOption.QRDecorationOptionColorLayer) {
		item(key = LazyContentKeys.OPTION__QR_EDIT_COLOR_LAYER) {
			QREditBlockSelectColorLayer(
				selected = decoration.coloredLayers(),
				onSelectLayer = { layer ->
					onDecorationChange(decoration.copy(coloredLayers = { layer }))
				},
				contentPadding = PaddingValues(dimensionResource(R.dimen.qr_edit_option_internal_padding)),
				shape = MaterialTheme.shapes.extraSmall.copy(
					bottomStart = MaterialTheme.shapes.large.bottomStart,
					bottomEnd = MaterialTheme.shapes.large.bottomEnd
				),
				modifier = Modifier.animateItem()
			)
		}
	} else {
		item(key = LazyContentKeys.OPTION_QR_EDIT_COLORS) {
			QREditDecorationColorOptions(
				decoration = decoration,
				onDecorationChange = onDecorationChange,
				contentPadding = PaddingValues(all = dimensionResource(R.dimen.qr_edit_option_internal_padding)),
				shape = MaterialTheme.shapes.extraSmall,
				modifier = Modifier.animateItem()
			)
		}
		item(key = LazyContentKeys.OPTION_QR_EDIT_BOOLEANS) {
			EditDecorationBooleanOptions(
				decoration = decoration,
				onDecorationChange = onDecorationChange,
				contentPadding = PaddingValues(all = dimensionResource(R.dimen.qr_edit_option_internal_padding)),
				shape = MaterialTheme.shapes.extraSmall.copy(
					bottomStart = MaterialTheme.shapes.large.bottomStart,
					bottomEnd = MaterialTheme.shapes.large.bottomEnd
				),
				modifier = Modifier.animateItem()
			)
		}
	}
}

enum class LazyContentKeys {
	OPTION_TEMPLATE,
	OPTION_EXPORT_DIMENSIONS,
	OPTION__QR_EDIT_COLOR_LAYER,
	OPTION_QR_EDIT_SLIDERS,
	OPTION_QR_EDIT_BOOLEANS,
	OPTION_QR_EDIT_COLORS
}

private class QRDecorationOptionPreviewParams :
	CollectionPreviewParameterProvider<QRDecorationOption>(
		listOf(
			QRDecorationOption.QRDecorationOptionBasic(),
			QRDecorationOption.QRDecorationOptionMinimal(),
			QRDecorationOption.QRDecorationOptionColorLayer()
		),
	)

@Preview
@Composable
private fun EditQRDecorationsBasicPreview(
	@PreviewParameter(QRDecorationOptionPreviewParams::class)
	decoration: QRDecorationOption,
) = QRForgeTheme {
	LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		editQRDecorationOptions(
			decoration = decoration,
			onDecorationChange = {},
		)
	}
}