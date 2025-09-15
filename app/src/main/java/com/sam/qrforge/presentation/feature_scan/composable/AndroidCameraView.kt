package com.sam.qrforge.presentation.feature_scan.composable

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.sam.qrforge.presentation.feature_scan.state.CameraFocusState

private val INDICATOR_SIZE = 48.dp

@Composable
fun AndroidCameraView(
	surfaceRequest: SurfaceRequest,
	tapToFocus: (Offset) -> Unit,
	onRelativeScaleChange: (Float) -> Unit,
	modifier: Modifier = Modifier,
	isFocusEnabled: Boolean = true,
	isZoomEnabled: Boolean = true,
	focusState: CameraFocusState = CameraFocusState.Unspecified,
) {
	val currentTapToFocus by rememberUpdatedState(tapToFocus)
	val currentOnScaleZoom by rememberUpdatedState(onRelativeScaleChange)

	val transformableState = rememberTransformableState { zoom, _, _ -> currentOnScaleZoom(zoom) }
	val coordinateTransformer = remember { MutableCoordinateTransformer() }

	CameraXViewfinder(
		surfaceRequest = surfaceRequest,
		coordinateTransformer = coordinateTransformer,
		modifier = modifier
			.transformable(state = transformableState, enabled = isZoomEnabled)
			.pointerInput(isFocusEnabled) {
				detectTapGestures { offset ->
					if (!isFocusEnabled) return@detectTapGestures
					with(coordinateTransformer) { currentTapToFocus(offset.transform()) }
				}
			},
	)

	if (focusState !is CameraFocusState.Specified) return

	val tapCoordinates = remember(coordinateTransformer.transformMatrix, focusState.coordinates) {
		val matrix = Matrix().apply {
			setFrom(coordinateTransformer.transformMatrix)
			invert()
		}
		matrix.map(focusState.coordinates)
	}

	AnimatedVisibility(
		visible = focusState.status == CameraFocusState.FocusStatus.RUNNING,
		enter = fadeIn(initialAlpha = .2f),
		exit = fadeOut(targetAlpha = .2f),
		modifier = Modifier
			.offset { tapCoordinates.round() }
			.offset(-INDICATOR_SIZE / 2, -INDICATOR_SIZE / 2)
	) {
		Box(
			modifier = Modifier
				.border(color = Color.White, width = 2.dp, shape = CircleShape)
				.size(INDICATOR_SIZE),
		)
	}
}
