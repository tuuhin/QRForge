package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraControlOption(
	onCapture: () -> Unit,
	onToggleFlash: () -> Unit,
	modifier: Modifier = Modifier,
	isFlashOn: Boolean = false,
	isManualCapture: Boolean = true,
	cameraState: CameraCaptureState = CameraCaptureState(),
	actionsEnabled: Boolean = true,
	onPickImage: () -> Unit = {},
) {

	Row(
		modifier = modifier
			.animateContentSize()
			.heightIn(110.dp),
		horizontalArrangement = Arrangement.SpaceAround,
		verticalAlignment = Alignment.CenterVertically,
	) {
		TooltipBox(
			positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
			tooltip = {
				PlainTooltip {
					Text(text = stringResource(R.string.camera_action_control_flash))
				}
			},
			state = rememberTooltipState()
		) {
			Surface(
				onClick = onToggleFlash,
				shape = MaterialTheme.shapes.large,
				color = MaterialTheme.colorScheme.secondary,
				contentColor = MaterialTheme.colorScheme.onSecondary,
				enabled = !cameraState.isCapturing && actionsEnabled,
				modifier = Modifier
					.size(52.dp)
					.semantics {
						role = Role.Button
					},
			) {
				AnimatedContent(
					targetState = isFlashOn,
					transitionSpec = {
						scaleIn(
							animationSpec = spring(
								Spring.DampingRatioLowBouncy,
								Spring.StiffnessLow
							)
						) togetherWith scaleOut(
							animationSpec = tween(durationMillis = 200, easing = EaseIn)
						)
					},
					contentAlignment = Alignment.Center,
					modifier = Modifier.padding(12.dp)
				) { isOn ->
					if (isOn)
						Icon(
							painter = painterResource(R.drawable.ic_flash_filled),
							contentDescription = "Flashing",
							modifier = Modifier.fillMaxSize()
						)
					else Icon(
						painter = painterResource(R.drawable.ic_no_flash),
						contentDescription = "No flashing",
						modifier = Modifier.fillMaxSize()
					)
				}
			}
		}
		AnimatedContent(
			targetState = isManualCapture,
			transitionSpec = {
				scaleIn(
					animationSpec = tween(durationMillis = 300, easing = EaseInCubic),
					initialScale = .2f,
				) + fadeIn(
					animationSpec = tween(durationMillis = 200, easing = EaseIn)
				) togetherWith scaleOut(
					animationSpec = tween(durationMillis = 300, easing = EaseOutCubic),
					targetScale = .2f
				) + fadeOut(animationSpec = tween(durationMillis = 200, easing = EaseOut))
			},
			contentAlignment = Alignment.Center,
			modifier = Modifier.defaultMinSize(72.dp),
		) { show ->
			if (show)
				CaptureButton(
					onClick = onCapture,
					enabled = !transition.isRunning && actionsEnabled,
					isCapturing = cameraState.isCapturing,
					captureProgress = cameraState.captureProgress,
					canReadProgress = cameraState.canPropagateProgress
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_scan),
						contentDescription = "Shutter",
						tint = Color.Black,
					)
				}
		}
		TooltipBox(
			positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
			tooltip = {
				PlainTooltip {
					Text(text = stringResource(R.string.camera_action_pick_image))
				}
			},
			state = rememberTooltipState()
		) {
			Surface(
				onClick = onPickImage,
				enabled = !cameraState.isCapturing && actionsEnabled,
				shape = MaterialTheme.shapes.large,
				color = MaterialTheme.colorScheme.secondary,
				contentColor = MaterialTheme.colorScheme.onSecondary,
				modifier = Modifier.size(52.dp),
			) {
				Icon(
					painter = painterResource(R.drawable.ic_gallery),
					contentDescription = stringResource(R.string.camera_action_pick_image),
					modifier = Modifier.padding(12.dp)
				)
			}
		}
	}
}