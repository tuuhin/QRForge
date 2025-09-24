package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionScopeProvider
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlin.math.abs
import kotlin.math.roundToInt

private const val ZOOM_PICKER_TRANSITION_KEY = "zoom-picker-shared-bounds"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CameraZoomPicker(
	zoomState: CameraZoomState,
	onZoomChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
	val isZoomRangeAvailable by remember(zoomState) {
		derivedStateOf { abs(zoomState.maxZoomRatio - zoomState.minZoomRatio) > 0 }
	}
//	// don't show the picker is zoom is not available
	if (!isZoomRangeAvailable) {
		ZoomValueButton(
			zoomLevel = zoomState.zoomRatio,
			enabled = false,
			onShowPicker = { },
			isZoomAvailable = false
		)
		return
	}

	var showPicker by remember { mutableStateOf(false) }

	AnimatedContent(
		targetState = showPicker,
		contentAlignment = Alignment.Center,
		modifier = modifier
	) { canShowPicker ->
		CompositionLocalProvider(LocalSharedTransitionVisibilityScopeProvider provides this) {
			if (canShowPicker) ZoomPickerSlider(
				zoomState = zoomState,
				onZoomChange = onZoomChange,
				containerColor = containerColor,
				enabled = enabled,
				onClosePicker = { showPicker = false },
				modifier = Modifier
					.fillMaxWidth(.85f)
					.sharedBoundsWrapper(
						key = ZOOM_PICKER_TRANSITION_KEY,
						boundsTransform = BoundsTransform { _, _ ->
							tween(durationMillis = 400, easing = EaseInOutExpo)
						}
					)
			)
			else ZoomValueButton(
				zoomLevel = zoomState.zoomRatio,
				onShowPicker = { showPicker = true },
				enabled = enabled,
				modifier = Modifier.sharedBoundsWrapper(
					key = ZOOM_PICKER_TRANSITION_KEY,
					clipShape = MaterialTheme.shapes.large,
					boundsTransform = BoundsTransform { _, _ ->
						tween(durationMillis = 400, easing = EaseInOutBounce)
					}
				)
			)
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoomValueButton(
	zoomLevel: () -> Float,
	modifier: Modifier = Modifier,
	onShowPicker: () -> Unit = {},
	enabled: Boolean = true,
	isZoomAvailable: Boolean = true,
) {
	val zoomLevelReadable by remember(zoomLevel) {
		derivedStateOf { (zoomLevel() * 100).roundToInt() / 100f }
	}

	TooltipBox(
		positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
		tooltip = {
			PlainTooltip {
				Text(
					text = if (isZoomAvailable) stringResource(R.string.camera_action_control_zoom)
					else stringResource(R.string.camera_action_control_zoom_absent)
				)
			}
		},
		state = rememberTooltipState(),
		modifier = modifier
	) {
		FilledTonalButton(
			onClick = onShowPicker,
			enabled = enabled,
			shape = MaterialTheme.shapes.extraLarge,
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
				disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
				disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
			),
			contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
		) {
			Text(
				text = stringResource(
					R.string.zoom_formated_2_decimal_place,
					zoomLevelReadable
				),
				style = MaterialTheme.typography.labelLarge,
			)
		}
	}
}

@Composable
private fun ZoomPickerSlider(
	zoomState: CameraZoomState,
	onZoomChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	onClosePicker: () -> Unit = {},
	enabled: Boolean = true,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		FilledTonalIconButton(
			onClick = { onZoomChange(zoomState.minZoomRatio) },
			colors = IconButtonDefaults.filledTonalIconButtonColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer
			),
			shape = MaterialTheme.shapes.large,
		) {
			Icon(
				painter = painterResource(R.drawable.ic_restore),
				contentDescription = "Close"
			)
		}
		Surface(
			shape = MaterialTheme.shapes.extraLarge,
			color = containerColor,
			modifier = Modifier.weight(1f),
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp, horizontal = 8.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(
					text = stringResource(
						R.string.zoom_formated_1_decimal_place,
						zoomState.minZoomRatio
					),
					style = MaterialTheme.typography.labelLarge,
					modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
				)
				Slider(
					value = zoomState.zoomRatio.invoke(),
					onValueChange = onZoomChange,
					enabled = enabled,
					valueRange = zoomState.zoomRange,
					modifier = Modifier.weight(1f)
				)
				Text(
					text = stringResource(
						R.string.zoom_formated_1_decimal_place,
						zoomState.maxZoomRatio
					),
					style = MaterialTheme.typography.labelLarge,
					modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
				)
			}
		}

		FilledTonalIconButton(
			onClick = onClosePicker,
			colors = IconButtonDefaults.filledTonalIconButtonColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer
			),
			shape = MaterialTheme.shapes.large,
		) {
			Icon(
				painter = painterResource(R.drawable.ic_clear),
				contentDescription = "Close"
			)
		}
	}
}

@Preview
@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
private fun CameraZoomPickerPreview() = QRForgeTheme {
	SharedTransitionLayout {
		CompositionLocalProvider(LocalSharedTransitionScopeProvider provides this) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.fillMaxWidth()
			) {
				CameraZoomPicker(
					zoomState = CameraZoomState(maxZoomRatio = 10f),
					onZoomChange = {},
					containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
					modifier = Modifier.padding(4.dp)
				)
			}
		}
	}
}