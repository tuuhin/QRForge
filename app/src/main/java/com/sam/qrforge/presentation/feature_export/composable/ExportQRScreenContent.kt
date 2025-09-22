package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents

@Composable
fun ExportQRScreenContent(
	generatedQR: GeneratedQRUIModel,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	showFaultyQRWarning: Boolean = false,
	dimensions: ExportDimensions = ExportDimensions.Medium,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	scrollState: LazyListState = rememberLazyListState(),
) {
	val layer = graphicsLayer?.invoke() ?: rememberGraphicsLayer()

	Column(
		modifier = modifier.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		AnimatedVisibility(
			visible = showFaultyQRWarning,
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut()
		) {
			FaultyQRWarningCard(modifier = Modifier.fillMaxWidth())
		}
		Surface(
			color = MaterialTheme.colorScheme.surfaceContainerLow,
			shape = MaterialTheme.shapes.extraLarge
		) {
			QRMasterTemplate(
				model = generatedQR,
				decoration = decoration,
				graphicsLayer = { layer },
				modifier = Modifier.size(260.dp)
			)
		}
		LazyColumn(
			state = scrollState,
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.weight(1f)
				.clip(MaterialTheme.shapes.large),
		) {
			item(key = LazyContentKeys.OPTION_EXPORT_DIMENSIONS) {
				ExportDimensionPicker(
					selected = dimensions,
					containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
					contentPadding = PaddingValues(dimensionResource(R.dimen.sc_padding)),
					onDimensionChange = {
						onEvent(ExportQRScreenEvents.OnExportDimensionChange(it))
					},
				)
			}
			item {
				Text(
					text = stringResource(R.string.select_template_title),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = dimensionResource(R.dimen.qr_edit_option_internal_padding))
				)
			}
			item(key = LazyContentKeys.OPTION_TEMPLATE) {
				QRTemplatePicker(
					selectedTemplate = decoration.templateType,
					onTemplateChange = { onEvent(ExportQRScreenEvents.OnQRTemplateChange(it)) },
					containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
					contentPadding = PaddingValues(dimensionResource(R.dimen.sc_padding)),
					modifier = Modifier.fillMaxWidth(),
				)
			}
			item {
				Text(
					text = stringResource(R.string.action_edit),
					style = MaterialTheme.typography.titleLarge,
					color = MaterialTheme.colorScheme.secondary,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = dimensionResource(R.dimen.qr_edit_option_internal_padding))
				)
			}
			editQRDecorationOptions(
				decoration = decoration,
				onDecorationChange = { onEvent(ExportQRScreenEvents.OnDecorationChange(it)) },
			)
		}
	}
}