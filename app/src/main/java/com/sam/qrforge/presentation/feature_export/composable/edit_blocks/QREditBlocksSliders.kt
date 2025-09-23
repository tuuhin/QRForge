package com.sam.qrforge.presentation.feature_export.composable.edit_blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QREditBlockRoundness(
	onRoundnessChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	initialRoundness: Float = 0f,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val currentOnRoundnessChange by rememberUpdatedState(onRoundnessChange)

	val sliderState = remember {
		SliderState(
			value = initialRoundness,
			onValueChangeFinished = {},
			steps = 10,
			valueRange = 0f..1f,
		)
	}

	LaunchedEffect(sliderState) {
		snapshotFlow { sliderState.value }
			.onEach { currentOnRoundnessChange(it) }
			.launchIn(this)
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
				color = MaterialTheme.colorScheme.tertiary,
				contentColor = MaterialTheme.colorScheme.onTertiary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = "${(sliderState.value * 10).roundToInt() * 10} %",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}
		}
		Slider(
			state = sliderState,
			colors = sliderColors
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QREditBlockContentMargin(
	onMarginChange: (Dp) -> Unit,
	modifier: Modifier = Modifier,
	maxMargin: Dp = 20.dp,
	initialMargin: Dp = 0.dp,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val density = LocalDensity.current

	val currentOnBitsSizeChanged by rememberUpdatedState(onMarginChange)

	val sliderState = remember {
		SliderState(
			value = with(density) {
				val current = initialMargin.toPx()
				(current / maxMargin.roundToPx()) * 10f
			},
			onValueChangeFinished = {},
			steps = 10,
			valueRange = 0f..10f,
		)
	}

	LaunchedEffect(sliderState) {
		snapshotFlow { sliderState.value }
			.map {
				with(density) { (maxMargin * it) / 10f }
			}
			.onEach { currentOnBitsSizeChanged(it) }
			.launchIn(this)
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
				color = MaterialTheme.colorScheme.tertiary,
				contentColor = MaterialTheme.colorScheme.onTertiary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = stringResource(
						R.string.quantity_unit_px,
						sliderState.value.roundToInt() * 2
					),
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}
		}
		Slider(
			state = sliderState,
			colors = sliderColors,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QREditBlockBitsSize(
	onBitsMultiplierChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	range: OpenEndRange<Float> = .5f..<1.5f,
	initialBitSize: Float = 1f,
	sliderColors: SliderColors = SliderDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	val currentOnBitsSizeChanged by rememberUpdatedState(onBitsMultiplierChange)

	val sliderState = remember(range) {
		SliderState(
			value = (initialBitSize * 10f).roundToInt() / 10f,
			onValueChangeFinished = {},
			steps = 10,
			valueRange = range.start..range.endExclusive,
		)
	}

	LaunchedEffect(sliderState) {
		snapshotFlow { sliderState.value }
			.onEach { currentOnBitsSizeChanged(it) }
			.launchIn(this)
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
				color = MaterialTheme.colorScheme.tertiary,
				contentColor = MaterialTheme.colorScheme.onTertiary,
				shape = MaterialTheme.shapes.extraLarge
			) {
				Text(
					text = "${(sliderState.value * 10f).roundToInt() / 10f} x",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
				)
			}
		}
		Slider(
			state = sliderState,
			colors = sliderColors,
		)
	}
}

@Composable
fun EditDecorationSliderOptions(
	initialValue: QRDecorationOption,
	onRoundnessChange: (Float) -> Unit,
	onMarginChange: (Dp) -> Unit,
	onBitsMultiplierChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			QREditBlockRoundness(
				initialRoundness = initialValue.roundness,
				onRoundnessChange = onRoundnessChange,
			)
			QREditBlockBitsSize(
				initialBitSize = initialValue.bitsSizeMultiplier,
				onBitsMultiplierChange = onBitsMultiplierChange
			)
			QREditBlockContentMargin(
				initialMargin = initialValue.contentMargin,
				onMarginChange = onMarginChange
			)
		}
	}
}