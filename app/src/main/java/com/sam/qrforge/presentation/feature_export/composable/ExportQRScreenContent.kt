package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.common.mappers.localeString
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents

@Composable
fun ExportQRScreenContent(
	generatedQR: GeneratedQRUIModel,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	isExportRunning: Boolean = false,
	dimensions: ExportDimensions = ExportDimensions.Medium,
	exportType: ImageMimeTypes = ImageMimeTypes.PNG,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	scrollState: LazyListState = rememberLazyListState(),
) {
	val layer = graphicsLayer?.invoke() ?: rememberGraphicsLayer()

	LazyColumn(
		state = scrollState,
		modifier = modifier.fillMaxSize(),
		contentPadding = contentPadding,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		if (isExportRunning) {
			item {
				LinearProgressIndicator(
					strokeCap = StrokeCap.Round,
					modifier = Modifier
						.fillMaxWidth()
						.animateItem(),
				)
			}
		}
		item {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Surface(
					color = MaterialTheme.colorScheme.surfaceContainerLow,
					shape = MaterialTheme.shapes.extraLarge
				) {
					QRMasterTemplate(
						model = generatedQR,
						decoration = decoration,
						graphicsLayer = { layer },
						modifier = Modifier.size(300.dp)
					)
				}
				SingleChoiceSegmentedButtonRow {
					ImageMimeTypes.entries.forEachIndexed { index, mime ->
						SegmentedButton(
							selected = exportType == mime,
							onClick = { onEvent(ExportQRScreenEvents.OnExportMimeTypeChange(mime)) },
							label = { Text(mime.localeString) },
							shape = SegmentedButtonDefaults.itemShape(
								index = index,
								count = ImageMimeTypes.entries.size
							),
						)
					}
				}
			}
		}
		item {
			ExportDimensionPicker(
				selected = dimensions,
				onDimensionChange = {
					onEvent(ExportQRScreenEvents.OnExportDimensionChange(it))
				},
			)
		}
		item {
			QRTemplatePicker(
				model = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
				selectedTemplate = decoration.templateType,
				onTemplateChange = { onEvent(ExportQRScreenEvents.OnQRTemplateChange(it)) },
				modifier = Modifier.fillMaxWidth(),
			)
		}
		item {
			EditQRDecoration(
				decoration = decoration,
				onDecorationChange = { onEvent(ExportQRScreenEvents.OnDecorationChange(it)) },
				modifier = Modifier.fillMaxWidth()
			)
		}
		item {
			FaultyQRWarningCard(modifier = Modifier.fillMaxWidth())
		}
	}
}