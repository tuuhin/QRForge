package com.sam.qrforge.presentation.feature_export.composable.edit_blocks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRColorLayer
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QREditBlockSelectColorLayer(
	onSelectLayer: (QRColorLayer) -> Unit,
	modifier: Modifier = Modifier,
	selected: QRColorLayer = QRColorLayer.PowerRangers,
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val colorLayers = remember { QRColorLayer.DEFAULT_OPTIONS }

	Column(
		verticalArrangement = Arrangement.spacedBy(2.dp),
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.qr_edit_property_layered_color_selector),
			style = titleStyle,
			color = titleColor,
		)
		Text(
			text = stringResource(R.string.qr_edit_property_layered_color_selector_text),
			style = bodyStyle,
			color = bodyColor,
		)
		Spacer(modifier = Modifier.height(8.dp))
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			colorLayers.forEach { layer ->
				CanvasColorLayered(
					layer = layer,
					onClick = { onSelectLayer(layer) },
					isSelected = selected.name == layer.name,
				)
			}
		}
	}
}

@Composable
private fun CanvasColorLayered(
	layer: QRColorLayer,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	selectedBorder: BorderStroke = BorderStroke(
		2.5.dp,
		MaterialTheme.colorScheme.onPrimaryContainer
	),
	shape: Shape = MaterialTheme.shapes.medium,
	isSelected: Boolean = false,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
	Box(
		modifier = modifier
			.size(72.dp)
			.clip(shape)
			.clickable(
				onClick = onClick,
				interactionSource = interactionSource,
				role = Role.Checkbox,
				indication = ripple()
			)
			.drawWithCache {
				val lastColors = with(layer.layers) {
					if (size > 4) this.takeLast(4)
					else this
				}

				val boxSize = size.times(.5f)
				val topLeftPosition = Offset(
					(size.width - boxSize.width) * .5f,
					(size.height - boxSize.width) * .5f,
				)

				val xAxisRange = 0f..(topLeftPosition.x + boxSize.width)
				val yAxisRange = 0f..(topLeftPosition.y + boxSize.height)

				onDrawBehind {

					lastColors.forEach { color ->
						drawRoundRect(
							color = color.color,
							cornerRadius = CornerRadius(10f, 10f),
							topLeft = Offset(
								(topLeftPosition.x * (1 + color.offset.x)).coerceIn(xAxisRange),
								(topLeftPosition.y * (1 + color.offset.y)).coerceIn(yAxisRange)
							),
							size = boxSize,
							blendMode = BlendMode.Difference,
							style = Stroke(width = 10.dp.toPx())
						)
					}

					// border
					drawOutline(
						outline = shape.createOutline(size, layoutDirection, this),
						brush = selectedBorder.brush,
						style = Stroke(width = selectedBorder.width.toPx()),
					)
					//draw outline
					if (isSelected) {
						drawOutline(
							outline = shape.createOutline(size, layoutDirection, this),
							brush = selectedBorder.brush,
							alpha = .3f,
						)
					}
				}
			},
		contentAlignment = Alignment.Center,
	) {
		AnimatedVisibility(
			visible = isSelected,
			enter = scaleIn(),
			exit = scaleOut()
		) {
			Icon(
				imageVector = Icons.Default.Check,
				contentDescription = null,
				modifier = Modifier.size(28.dp),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun QREditBlockSelectColorLayerPreview() = QRForgeTheme {

	var selected by remember { mutableStateOf<QRColorLayer>(QRColorLayer.PowerRangers) }

	Surface {
		QREditBlockSelectColorLayer(
			onSelectLayer = { selected = it },
			selected = selected,
			modifier = Modifier.padding(16.dp),
		)
	}
}
