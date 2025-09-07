package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
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
	contentPadding: PaddingValues = PaddingValues(16.dp),
) {
	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
		) {
			Text(
				text = stringResource(R.string.select_template_title),
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSecondaryContainer,
			)
			LazyRow(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				contentPadding = PaddingValues(top = 8.dp),
				modifier = Modifier.fillMaxWidth(),
			) {
				itemsIndexed(
					items = QRTemplateOption.entries,
					key = { _, item -> item.name },
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
	borderStroke: BorderStroke = BorderStroke(2.5.dp, MaterialTheme.colorScheme.onPrimaryContainer),
	selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
	templateTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
	templateTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
	shape: Shape = MaterialTheme.shapes.medium,
) {
	Surface(
		shape = shape,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
		onClick = onTemplateChange,
		modifier = modifier,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.drawWithContent {
				if (isSelected) {
					// overlay
					drawOutline(
						outline = shape.createOutline(size, layoutDirection, this),
						color = selectedContainerColor,
					)
					// border
					drawOutline(
						outline = shape.createOutline(size, layoutDirection, this),
						brush = borderStroke.brush,
						style = Stroke(width = borderStroke.width.toPx())
					)
				}
				scale(.9f) {
					this@drawWithContent.drawContent()
				}
			},
		) {
			Box(
				modifier = Modifier
					.size(templateSize)
					.padding(4.dp),
			) {
				when (template) {
					QRTemplateOption.BASIC -> QRTemplateBasic(
						model = renderModel,
						modifier = Modifier.matchParentSize(),
					)

					QRTemplateOption.MINIMALISTIC -> QRTemplateMinimalistic(
						model = renderModel,
						modifier = Modifier.matchParentSize()
					)

					QRTemplateOption.COLOR_LAYERED -> QRTemplateLayered(
						model = renderModel,
						modifier = Modifier.matchParentSize()
					)
				}
			}
			Text(
				text = template.localeString,
				style = templateTextStyle,
				color = templateTextColor
			)
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