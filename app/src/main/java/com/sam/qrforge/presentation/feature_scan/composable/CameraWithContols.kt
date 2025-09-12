package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.presentation.feature_scan.state.CaptureType

@Composable
fun CameraWithControls(
	cameraContent: @Composable BoxScope.() -> Unit,
	modifier: Modifier = Modifier,
	captureType: CaptureType = CaptureType.AUTO,
	zoomState: CameraZoomState = CameraZoomState(),
	isFlashOn: Boolean = false,
	onCaptureTypeChange: (CaptureType) -> Unit = {},
	onZoomChange: (Float) -> Unit = {},
	onCaptureImage: () -> Unit = {},
	onToggleFlash: () -> Unit = {},
) {

	var showZoomPicker by remember { mutableStateOf(false) }

	Column(
		modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.clip(
					shape = MaterialTheme.shapes.extraLarge.copy(
						topStart = ZeroCornerSize, topEnd = ZeroCornerSize
					),
				)
		) {
			// camera content
			cameraContent()
			// capture overlay
			CameraCaptureOverlayBox(modifier = Modifier.align(Alignment.Center))
			Column(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(bottom = 20.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				AnimatedVisibility(
					visible = showZoomPicker,
					enter = slideInVertically() + fadeIn(),
					exit = slideOutVertically() + fadeOut(),
					modifier = Modifier.fillMaxWidth(.9f)
				) {
					CameraZoomPicker(
						zoomState = zoomState,
						onZoomChange = onZoomChange,
						onClosePicker = { showZoomPicker = false },
					)
				}
				// actions
				CameraControlOption(
					isFlashOn = isFlashOn,
					showCaptureButton = captureType == CaptureType.MANUAL,
					onCapture = onCaptureImage,
					onToggleFlash = onToggleFlash,
					zoomLevel = { zoomState.zoomRatio() },
					onShowZoomPicker = { showZoomPicker = true },
					modifier = Modifier.fillMaxWidth(.85f)
				)
			}
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(height = 80.dp),
			contentAlignment = Alignment.Center,
		) {
			// buttons
			SingleChoiceSegmentedButtonRow {
				CaptureType.entries.forEachIndexed { index, type ->
					SegmentedButton(
						selected = captureType == type,
						onClick = { onCaptureTypeChange(type) },
						label = { Text(type.name) },
						shape = SegmentedButtonDefaults.itemShape(
							index = index, count = ImageMimeTypes.entries.size
						),
					)
				}
			}
		}
	}
}