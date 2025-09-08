package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun ColorOptionSelector(
	onColorChange: (Color) -> Unit,
	modifier: Modifier = Modifier,
	selected: Color = Color.Transparent,
	subheadingStyle: TextStyle = MaterialTheme.typography.labelLarge,
	subheadingColor: Color = MaterialTheme.colorScheme.onSurface,
	contentPadding: PaddingValues = PaddingValues(8.dp),
) {
	val themedColors = listOf(
		Color.Transparent,
		MaterialTheme.colorScheme.onBackground,
		MaterialTheme.colorScheme.primaryContainer,
		MaterialTheme.colorScheme.secondaryContainer,
		MaterialTheme.colorScheme.tertiaryContainer,
		MaterialTheme.colorScheme.onPrimaryContainer,
		MaterialTheme.colorScheme.onSecondaryContainer,
		MaterialTheme.colorScheme.onTertiaryContainer,
	)

	val lightColors = listOf(
		colorResource(R.color.tailwind_color_red_light),
		colorResource(R.color.tailwind_color_orange_light),
		colorResource(R.color.tailwind_color_amber_light),
		colorResource(R.color.tailwind_color_yellow_light),
		colorResource(R.color.tailwind_color_lime_light),
		colorResource(R.color.tailwind_color_green_light),
		colorResource(R.color.tailwind_color_emerald_light),
		colorResource(R.color.tailwind_color_teal_light),
		colorResource(R.color.tailwind_color_cyan_light),
		colorResource(R.color.tailwind_color_sky_light),
		colorResource(R.color.tailwind_color_blue_light),
		colorResource(R.color.tailwind_color_indigo_light),
		colorResource(R.color.tailwind_color_violet_light),
		colorResource(R.color.tailwind_color_purple_light),
		colorResource(R.color.tailwind_color_pink_light),
		colorResource(R.color.tailwind_color_rose_light),
	)

	val darkColors = listOf(
		colorResource(R.color.tailwind_color_red_dark),
		colorResource(R.color.tailwind_color_orange_dark),
		colorResource(R.color.tailwind_color_amber_dark),
		colorResource(R.color.tailwind_color_yellow_dark),
		colorResource(R.color.tailwind_color_lime_dark),
		colorResource(R.color.tailwind_color_green_dark),
		colorResource(R.color.tailwind_color_emerald_dark),
		colorResource(R.color.tailwind_color_teal_dark),
		colorResource(R.color.tailwind_color_cyan_dark),
		colorResource(R.color.tailwind_color_sky_dark),
		colorResource(R.color.tailwind_color_blue_dark),
		colorResource(R.color.tailwind_color_indigo_dark),
		colorResource(R.color.tailwind_color_violet_dark),
		colorResource(R.color.tailwind_color_purple_dark),
		colorResource(R.color.tailwind_color_pink_dark),
		colorResource(R.color.tailwind_color_rose_dark),
	)

	LazyVerticalGrid(
		columns = GridCells.Fixed(8),
		contentPadding = contentPadding,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
	) {
		item(
			span = { GridItemSpan(maxLineSpan) },
			contentType = "Title"
		) {
			Text(
				text = "Colors",
				style = subheadingStyle,
				color = subheadingColor,
			)
		}
		itemsIndexed(
			items = themedColors,
			contentType = { _, _ -> Color::class.java },
		) { _, color ->
			ColorOption(
				color = color,
				onSelectColor = { onColorChange(color) },
				isSelected = color == selected,
			)
		}
		item(
			span = { GridItemSpan(maxLineSpan) },
			contentType = "Title",
		) {
			Text(
				text = "Light Colors",
				style = subheadingStyle,
				color = subheadingColor,
			)
		}
		itemsIndexed(
			items = lightColors,
			contentType = { _, _ -> Color::class.java },
		) { _, color ->
			ColorOption(
				color = color,
				onSelectColor = { onColorChange(color) },
				isSelected = color == selected
			)
		}
		item(
			span = { GridItemSpan(maxLineSpan) },
			contentType = "Title",
		) {
			Text(
				text = "Dark Colors",
				style = subheadingStyle,
				color = subheadingColor,
			)
		}
		itemsIndexed(
			items = darkColors,
			contentType = { _, _ -> Color::class.java },
		) { _, color ->
			ColorOption(
				color = color,
				onSelectColor = { onColorChange(color) },
				isSelected = color == selected
			)
		}
	}
}

@Composable
private fun ColorOption(
	color: Color,
	onSelectColor: () -> Unit,
	isSelected: Boolean,
	modifier: Modifier = Modifier,
	borderStoke: BorderStroke = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
	shape: Shape = MaterialTheme.shapes.large,
) {

	val transparentPainter = painterResource(R.drawable.ic_transparent)
	val painterColor = MaterialTheme.colorScheme.tertiary

	Box(
		modifier = modifier
			.size(42.dp)
			.clip(shape)
			.clickable(onClick = onSelectColor)
			.aspectRatio(1f)
			.drawBehind {
				// draw the circle
				if (color == Color.Transparent) {
					scale(.5f) {
						with(transparentPainter) {
							draw(size = size, colorFilter = ColorFilter.tint(painterColor))
						}
					}
				}
				//  color outline
				drawOutline(
					outline = shape.createOutline(size, layoutDirection, this),
					color = if (color == Color.Transparent)
						Color.Gray.copy(alpha = .2f)
					else color
				)
				// draw the border
				if (isSelected) {
					drawOutline(
						outline = shape.createOutline(size, layoutDirection, this),
						brush = borderStoke.brush,
						style = Stroke(
							width = borderStoke.width.toPx()
						)
					)
				}
			}
	)
}


@PreviewLightDark
@Composable
private fun ColorOptionSelectorPreview() = QRForgeTheme {
	Surface {
		ColorOptionSelector(
			onColorChange = {},
			modifier = Modifier.fillMaxWidth()
		)
	}
}