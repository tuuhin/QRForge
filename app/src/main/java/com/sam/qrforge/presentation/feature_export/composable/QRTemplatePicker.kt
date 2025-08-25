package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRColorLayer
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.templates.QRTemplateLayered
import com.sam.qrforge.presentation.common.templates.QRTemplateMinimalistic
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRTemplatePicker(
	model: GeneratedQRUIModel,
	onTemplateChange: (QRTemplateOption) -> Unit,
	modifier: Modifier = Modifier,
	selectedTemplate: QRTemplateOption = QRTemplateOption.BASIC,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(12.dp),
) {
	Surface(
		shape = shape,
		color = containerColor,
		contentColor = contentColorFor(containerColor),
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				text = "Select template",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
			LazyRow(
				horizontalArrangement = Arrangement.spacedBy(6.dp)
			) {
				itemsIndexed(
					items = QRTemplateOption.entries,
					key = { _, item -> item.name },
					contentType = { _, item -> item.javaClass.simpleName },
				) { _, template ->
					QRTemplateOption(
						renderModel = model,
						onTemplateChange = { onTemplateChange(template) },
						template = template,
						isSelected = selectedTemplate == template
					)
				}
			}
		}
	}
}

@Composable
private fun QRTemplateOption(
	renderModel: GeneratedQRUIModel,
	onTemplateChange: () -> Unit,
	modifier: Modifier = Modifier,
	template: QRTemplateOption = QRTemplateOption.MINIMALISTIC,
	isSelected: Boolean = false,
	templateSize: Dp = 80.dp,
	selectedBorder: BorderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
	selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
	Surface(
		shape = MaterialTheme.shapes.medium,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
		onClick = onTemplateChange,
		modifier = modifier,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.drawWithContent {

				val borderWidth = selectedBorder.width.toPx()

				if (isSelected) {
					drawRoundRect(
						color = selectedContainerColor,
						cornerRadius = CornerRadius(10.dp.toPx())
					)
					drawRoundRect(
						topLeft = Offset(borderWidth * .5f, borderWidth * .5f),
						size = Size(
							width = size.width - borderWidth,
							height = size.height - borderWidth
						),
						brush = selectedBorder.brush,
						cornerRadius = CornerRadius(10.dp.toPx()),
						style = Stroke(width = selectedBorder.width.toPx())
					)
				}
				scale(.9f) {
					this@drawWithContent.drawContent()
				}
			},
		) {
			when (template) {
				QRTemplateOption.BASIC -> {
					QRTemplateBasic(
						model = renderModel,
						backgroundColor = Color.Transparent,
						modifier = Modifier.size(templateSize),
					)
					Text(
						text = "Basic",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}

				QRTemplateOption.MINIMALISTIC -> {
					QRTemplateMinimalistic(
						model = renderModel,
						backgroundColor = Color.Transparent,
						modifier = Modifier.size(templateSize)
					)
					Text(
						text = "Minimal",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}

				QRTemplateOption.COLOR_LAYERED -> {
					QRTemplateLayered(
						model = renderModel,
						coloredLayers = { QRColorLayer.COLOR_BLOCKS },
						backgroundColor = Color.Transparent,
						modifier = Modifier.size(templateSize)
					)
					Text(
						text = "Layered",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun QRTemplatePickerPreview() = QRForgeTheme {
	QRTemplatePicker(
		model = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
		onTemplateChange = {},
		modifier = Modifier.fillMaxWidth()
	)
}