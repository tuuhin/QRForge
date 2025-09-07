package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import kotlin.math.roundToInt

@Composable
fun QREditBlockRoundness(
	roundness: Float,
	onRoundnessChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(2.dp),
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
			Text(
				text = "${(roundness * 100).roundToInt()} %",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
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
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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
		verticalArrangement = Arrangement.spacedBy(2.dp),
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
			Text(
				text = "$marginAsPx px",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
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
	range: ClosedRange<Float> = .2f..1.5f,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val multipleValue = remember(bitsSizeMultiplier) {
		(bitsSizeMultiplier * 10f).roundToInt() / 10f
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(2.dp),
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
			Text(
				text = "$multipleValue x",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
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
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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