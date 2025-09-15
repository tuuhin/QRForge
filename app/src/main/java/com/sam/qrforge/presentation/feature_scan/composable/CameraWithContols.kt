package com.sam.qrforge.presentation.feature_scan.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_scan.state.CameraCaptureState
import com.sam.qrforge.presentation.feature_scan.state.CameraControlsState
import com.sam.qrforge.presentation.feature_scan.state.CaptureType
import com.sam.qrforge.presentation.feature_scan.state.ImageAnalysisState

@Composable
fun CameraWithControls(
	cameraContent: @Composable BoxScope.() -> Unit,
	modifier: Modifier = Modifier,
	cameraControls: CameraControlsState = CameraControlsState(),
	cameraState: CameraCaptureState = CameraCaptureState(),
	analysisState: ImageAnalysisState = ImageAnalysisState(),
	onCaptureTypeChange: (CaptureType) -> Unit = {},
	onZoomChange: (Float) -> Unit = {},
	onCaptureImage: () -> Unit = {},
	onToggleFlash: () -> Unit = {},
	onSelectGalleryImage: (String) -> Unit = {},
) {

	val currentOnSelectImage by rememberUpdatedState(onSelectGalleryImage)

	val cameraContainerShape = MaterialTheme.shapes.extraLarge.copy(
		topStart = ZeroCornerSize, topEnd = ZeroCornerSize
	)

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = { uri -> uri?.let { currentOnSelectImage(it.toString()) } },
	)

	Column(
		modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(shape = cameraContainerShape)
				.weight(1f)
		) {
			// camera content
			cameraContent()
			// other content
			Box(
				modifier = Modifier
					.matchParentSize()
					.padding(20.dp)
					.windowInsetsPadding(WindowInsets.systemBars),
				contentAlignment = Alignment.Center
			) {
				// capture overlay
				CameraCaptureOverlayBox()
				// chip
				AnalyzerRunningChip(
					isRunning = analysisState.isAnalyzerRunning,
					modifier = Modifier.align(Alignment.TopCenter),
				)
				Column(
					modifier = Modifier.align(Alignment.BottomCenter),
					verticalArrangement = Arrangement.spacedBy(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					CameraZoomPicker(
						zoomState = cameraControls.zoomState,
						onZoomChange = onZoomChange,
					)
					// actions
					CameraControlOption(
						isFlashOn = cameraControls.isFlashEnabled,
						showCaptureButton = analysisState.captureType == CaptureType.MANUAL,
						onCapture = onCaptureImage,
						onToggleFlash = onToggleFlash,
						cameraState = cameraState,
						onPickImage = {
							val request = PickVisualMediaRequest.Builder()
								.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
								.setDefaultTab(ActivityResultContracts.PickVisualMedia.DefaultTab.PhotosTab)
								.build()
							launcher.launch(request)
						},
						modifier = Modifier.fillMaxWidth(.85f)
					)
				}
			}
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(height = 80.dp),
			contentAlignment = Alignment.Center,
		) {
			// buttons
			CameraCaptureTypePicker(
				onCaptureTypeChange = onCaptureTypeChange,
				selected = analysisState.captureType,
				enabled = !cameraState.isCapturing
			)
		}
	}
}


@Composable
private fun AnalyzerRunningChip(
	isRunning: Boolean,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(
		visible = isRunning,
		modifier = modifier,
		enter = slideInVertically() + scaleIn(
			initialScale = .2f,
			animationSpec = tween(durationMillis = 600, easing = EaseInExpo)
		),
		exit = scaleOut(
			targetScale = .2f,
			animationSpec = tween(durationMillis = 400, easing = EaseOutBack)
		) + slideOutVertically()
	) {
		SuggestionChip(
			onClick = {},
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_eye_simplified),
					contentDescription = "Looking"
				)
			},
			shape = MaterialTheme.shapes.extraLarge,
			enabled = false,
			label = { Text(stringResource(R.string.analyzer_running_text)) },
			colors = SuggestionChipDefaults.suggestionChipColors(
				disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
				disabledLabelColor = MaterialTheme.colorScheme.tertiary,
				disabledIconContentColor = MaterialTheme.colorScheme.tertiary
			)
		)
	}
}