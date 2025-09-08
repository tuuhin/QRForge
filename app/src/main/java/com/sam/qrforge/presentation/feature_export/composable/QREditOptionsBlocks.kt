package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun QREditBlockRoundness(
	roundness: Float,
	onRoundnessChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = modifier,
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = stringResource(R.string.qr_edit_property_roundness_title),
					style = titleStyle,
					color = titleColor,
				)
				Text(
					text = stringResource(R.string.qr_edit_property_roundness_text),
					style = bodyStyle,
					color = bodyColor
				)
			}
			Surface(
				color = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.secondary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = "${(roundness * 10).roundToInt() * 10} %",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}
		}
		Slider(
			value = roundness.coerceIn(0f..1f),
			onValueChange = { current -> onRoundnessChange(current) },
			steps = 10,
			valueRange = 0f..1f,
			colors = sliderColors,
		)
	}
}

@Composable
fun QREditBlockContentMargin(
	contentMargin: Dp,
	onMarginChange: (Dp) -> Unit,
	modifier: Modifier = Modifier,
	maxMargin: Dp = 20.dp,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val density = LocalDensity.current

	val contentMarginSimple = remember(contentMargin) {
		with(density) {
			val current = contentMargin.toPx()
			(current / maxMargin.roundToPx()) * 10f
		}
	}

	val marginAsPx = remember(contentMargin) {
		with(density) { contentMargin.roundToPx() }
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = modifier,
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = stringResource(R.string.qr_edit_property_margin_title),
					style = titleStyle,
					color = titleColor
				)
				Text(
					text = stringResource(R.string.qr_edit_property_margin_text),
					style = bodyStyle,
					color = bodyColor
				)
			}
			Surface(
				color = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.secondary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = "$marginAsPx px",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}
		}
		Slider(
			value = contentMarginSimple.coerceIn(0f..10f),
			onValueChange = { current ->
				val margin = with(density) { (maxMargin * current) / 10f }
				onMarginChange(margin)
			},
			steps = 10,
			valueRange = 0f..10f,
			colors = sliderColors,
		)
	}
}

@Composable
fun QREditBlockBitsSize(
	bitsSizeMultiplier: Float,
	onBitsMultiplierChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	range: ClosedRange<Float> = .5f..1.5f,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val multipleValue = remember(bitsSizeMultiplier) {
		(bitsSizeMultiplier * 10f).roundToInt() / 10f
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = modifier
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = stringResource(R.string.qr_edit_property_bits_size_title),
					style = titleStyle,
					color = titleColor
				)
				Text(
					text = stringResource(R.string.qr_edit_property_bits_size_text),
					style = bodyStyle,
					color = bodyColor
				)
			}
			Surface(
				color = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.secondary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = "$multipleValue x",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}

		}
		Slider(
			value = bitsSizeMultiplier.coerceIn(range),
			onValueChange = onBitsMultiplierChange,
			steps = 10,
			valueRange = range.start..range.endInclusive,
			colors = sliderColors,
		)
	}
}

@Composable
fun QREditBlockShowFrame(
	showFrame: Boolean,
	onShowFrameChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	switchColors: SwitchColors = SwitchDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = stringResource(R.string.qr_edit_property_show_frame_title),
				style = titleStyle,
				color = titleColor
			)
			Text(
				text = stringResource(R.string.qr_edit_property_show_frame_text),
				style = bodyStyle,
				color = bodyColor
			)
		}
		Switch(
			checked = showFrame,
			onCheckedChange = { isChecked -> onShowFrameChange(showFrame) },
			colors = switchColors
		)
	}
}

@Composable
fun QREditBlockFinderShape(
	isShapeDiamond: Boolean,
	onChangeShape: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	switchColors: SwitchColors = SwitchDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = stringResource(R.string.qr_edit_property_finder_shape_diamond_title),
				style = titleStyle,
				color = titleColor
			)
			Text(
				text = stringResource(R.string.qr_edit_property_finder_shape_diamond_text),
				style = bodyStyle,
				color = bodyColor
			)
		}
		Switch(
			checked = isShapeDiamond,
			onCheckedChange = { isChecked -> onChangeShape(isChecked) },
			colors = switchColors
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QREditBlockSelectColor(
	title: String,
	selectedColor: Color,
	onSelectColor: (Color) -> Unit,
	modifier: Modifier = Modifier,
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
) {

	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()

	var showSheet by remember { mutableStateOf(false) }

	if (showSheet) {
		ModalBottomSheet(
			onDismissRequest = { showSheet = false },
			sheetState = sheetState,
		) {
			Column(
				modifier = Modifier.padding(dimensionResource(R.dimen.bottom_sheet_content_padding)),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ColorOptionSelector(
					onColorChange = { color -> onSelectColor(color) },
					selected = selectedColor,
					contentPadding = PaddingValues.Zero
				)
				Button(
					onClick = {
						scope.launch { sheetState.hide() }
							.invokeOnCompletion { showSheet = false }
					},
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
					shape = MaterialTheme.shapes.extraLarge,
					modifier = Modifier.align(Alignment.End)
				) {
					Text(text = stringResource(R.string.action_done))
				}
			}
		}
	}

	val background = if (selectedColor == Color.Transparent)
		MaterialTheme.colorScheme.surfaceVariant
	else selectedColor

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		modifier = modifier,
	) {
		Text(
			text = title,
			style = titleStyle,
			color = titleColor,
			modifier = Modifier.weight(1f)
		)
		Box(
			modifier = Modifier
				.padding(all = 4.dp)
				.size(40.dp)
				.background(
					color = background,
					shape = MaterialTheme.shapes.large
				)
				.clip(MaterialTheme.shapes.large)
				.clickable(
					onClick = {
						scope.launch { sheetState.show() }
							.invokeOnCompletion { showSheet = true }
					},
				),
			contentAlignment = Alignment.Center,
		) {
			if (selectedColor == Color.Transparent) {
				Icon(
					painter = painterResource(R.drawable.ic_transparent),
					contentDescription = "Transparent Icon",
					modifier = Modifier.scale(.8f)
				)
			}
		}
	}
}
