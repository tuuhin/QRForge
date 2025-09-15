package com.sam.qrforge.presentation.feature_scan.composable

import androidx.camera.core.SurfaceRequest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sam.qrforge.presentation.common.utils.sharedTransitionKeepChildSize
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState
import com.sam.qrforge.presentation.feature_scan.state.CameraControllerEvents
import com.sam.qrforge.presentation.feature_scan.state.CameraControlsState
import com.sam.qrforge.presentation.feature_scan.state.CameraFocusState
import com.sam.qrforge.presentation.feature_scan.state.ImageAnalysisState

@Composable
fun ScanQRScreenContent(
	cameraCaptureState: CameraCaptureState,
	cameraControlState: CameraControlsState,
	analyzerState: ImageAnalysisState,
	onEvent: (CameraControllerEvents) -> Unit,
	modifier: Modifier = Modifier,
) {

	val lifecycleOwner = LocalLifecycleOwner.current

	LifecycleStartEffect(key1 = Unit, lifecycleOwner = lifecycleOwner) {
		// binds the camera
		onEvent(CameraControllerEvents.BindCamera)

		onStopOrDispose {
			// on unbind the camera when done
			onEvent(CameraControllerEvents.UnBindCamera)
		}
	}

	CameraWithControls(
		cameraContent = {
			AnimatedContent(
				targetState = cameraCaptureState.postCapturePreview != null,
				transitionSpec = {
					fadeIn(
						animationSpec = tween(durationMillis = 400, easing = EaseInBack)
					) + scaleIn(initialScale = .5f) togetherWith fadeOut(
						animationSpec = tween(durationMillis = 400, easing = EaseOutCubic)
					) + scaleOut()
				},
				contentAlignment = Alignment.Center,
				modifier = Modifier.sharedTransitionKeepChildSize()
			) { isPreviewImage ->
				if (isPreviewImage && cameraCaptureState.postCapturePreview != null) {
					Image(
						bitmap = cameraCaptureState.postCapturePreview.asImageBitmap(),
						contentDescription = "Capture Preview",
						modifier = Modifier.matchParentSize()
					)
				} else {
					CameraContent(
						surfaceRequest = cameraControlState.surfaceRequest,
						focusState = cameraControlState.focusState,
						onTapToFocus = { focusOffset ->
							onEvent(CameraControllerEvents.TapToFocus(focusOffset))
						},
						onRelativeZoom = { zoomFraction ->
							onEvent(CameraControllerEvents.OnZoomLevelChange(zoomFraction, true))
						},
						modifier = Modifier.matchParentSize()
					)
				}
			}
		},
		cameraControls = cameraControlState,
		cameraState = cameraCaptureState,
		analysisState = analyzerState,
		onZoomChange = { onEvent(CameraControllerEvents.OnZoomLevelChange(it)) },
		onToggleFlash = { onEvent(CameraControllerEvents.ToggleFlash) },
		onCaptureImage = { onEvent(CameraControllerEvents.CaptureImage) },
		onCaptureTypeChange = { onEvent(CameraControllerEvents.OnChangeCaptureMode(it)) },
		onSelectGalleryImage = { uri -> onEvent(CameraControllerEvents.OnSelectImageURI(uri)) },
		modifier = modifier,
	)
}

@Composable
private fun CameraContent(
	surfaceRequest: SurfaceRequest?,
	onTapToFocus: (Offset) -> Unit,
	onRelativeZoom: (Float) -> Unit,
	modifier: Modifier = Modifier,
	focusState: CameraFocusState = CameraFocusState.Unspecified,
) {
	val isInspectionMode = LocalInspectionMode.current

	if (isInspectionMode) {
		Box(
			modifier = modifier
				.background(MaterialTheme.colorScheme.surfaceContainerHighest)
		)
	} else surfaceRequest?.let { request ->
		AndroidCameraView(
			surfaceRequest = request,
			focusState = focusState,
			onRelativeScaleChange = onRelativeZoom,
			tapToFocus = onTapToFocus,
			modifier = modifier,
		)
	}
}