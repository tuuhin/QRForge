package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntSize
import androidx.compose.ui.unit.toSize
import com.sam.qrforge.R

@Composable
fun CameraControlOption(
	onCapture: () -> Unit,
	onToggleFlash: () -> Unit,
	modifier: Modifier = Modifier,
	isFlashOn: Boolean = false,
	showCaptureButton: Boolean = true,
	zoomLevel: () -> Float = { 1f },
	onShowZoomPicker: () -> Unit = {},
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceAround,
		verticalAlignment = Alignment.CenterVertically,
	) {
		FilledTonalButton(
			onClick = onToggleFlash,
			shape = MaterialTheme.shapes.extraLarge,
			contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.onSecondaryContainer
			)
		) {
			AnimatedContent(
				targetState = isFlashOn,
				transitionSpec = {
					slideInVertically() + expandVertically() togetherWith slideOutVertically() + shrinkVertically()
				},
				contentAlignment = Alignment.Center,
			) { isOn ->
				if (isOn)
					Icon(
						painter = painterResource(R.drawable.ic_flash_filled),
						contentDescription = "Flashing"
					)
				else Icon(
					painter = painterResource(R.drawable.ic_no_flash),
					contentDescription = "No flashing"
				)
			}
		}
		AnimatedContent(
			targetState = showCaptureButton,
			transitionSpec = {
				expandIn(
					animationSpec = tween(durationMillis = 200, easing = EaseInCubic),
					expandFrom = Alignment.Center,
					initialSize = { it.toSize().times(.2f).roundToIntSize() }
				) + fadeIn() togetherWith shrinkOut(
					animationSpec = tween(durationMillis = 200, easing = EaseInCubic),
					shrinkTowards = Alignment.Center,
					targetSize = { it.toSize().times(.2f).roundToIntSize() }
				) + fadeOut()
			},
			contentAlignment = Alignment.Center,
			modifier = Modifier.defaultMinSize(minWidth = 72.dp, minHeight = 72.dp)
		) { isReady ->
			if (isReady)
				CaptureButton(
					onClick = onCapture,
					isAnimating = !transition.isRunning
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_scan),
						contentDescription = "Shutter",
						tint = MaterialTheme.colorScheme.surfaceTint,
					)
				}

		}
		FilledTonalButton(
			onClick = onShowZoomPicker,
			contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.onSecondaryContainer
			)
		) {
			Text(text = stringResource(R.string.zoom_formated_2_decimal_place, zoomLevel()))
		}
	}
}