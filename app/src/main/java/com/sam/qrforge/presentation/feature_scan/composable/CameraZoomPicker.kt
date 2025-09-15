package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSharedTransitionVisibilityScopeProvider
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlin.math.roundToInt

private const val ZOOM_PICKER_TRANSITION_KEY = "zoom-picker-shared-bounds"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CameraZoomPicker(
	zoomState: CameraZoomState,
	onZoomChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {

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
				onClosePicker = { showPicker = false },
				modifier = Modifier
					.fillMaxWidth(.85f)
					.sharedBoundsWrapper(
						key = ZOOM_PICKER_TRANSITION_KEY,
						resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
						placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
						boundsTransform = BoundsTransform { initial, target ->
							keyframes {
								durationMillis = 400
								initial at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
								target at 400
							}
						}
					)
			) else {
				ZoomValueButton(
					zoomLevel = zoomState.zoomRatio,
					onShowPicker = { showPicker = true },
					modifier = Modifier.sharedBoundsWrapper(
						key = ZOOM_PICKER_TRANSITION_KEY,
						resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
						placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize,
						boundsTransform = BoundsTransform { initial, target ->
							keyframes {
								durationMillis = 400
								initial at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
								target at 400
							}
						}
					)
				)
			}
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoomValueButton(
	zoomLevel: () -> Float,
	modifier: Modifier = Modifier,
	onShowPicker: () -> Unit = {}
) {
	val zoomLevelReadable by remember(zoomLevel) {
		derivedStateOf { (zoomLevel() * 100).roundToInt() / 100f }
	}

	TooltipBox(
		positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
		tooltip = {
			PlainTooltip {
				Text(text = stringResource(R.string.camera_action_control_zoom))
			}
		},
		state = rememberTooltipState(),
		modifier = modifier
	) {
		FilledTonalButton(
			onClick = onShowPicker,
			shape = MaterialTheme.shapes.extraLarge,
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
			),
			contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
		) {
			Text(
				text = stringResource(
					R.string.zoom_formated_2_decimal_place,
					zoomLevelReadable
				),
				style = MaterialTheme.typography.labelLarge
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
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
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

@PreviewLightDark
@Composable
private fun CameraZoomPickerPreview() = QRForgeTheme {
	CameraZoomPicker(
		zoomState = CameraZoomState(maxZoomRatio = 10f),
		onZoomChange = {},
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier.padding(4.dp)
	)
}