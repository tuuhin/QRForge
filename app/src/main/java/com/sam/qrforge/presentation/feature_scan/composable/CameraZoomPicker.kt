package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_scan.state.CameraZoomState
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun CameraZoomPicker(
	zoomState: CameraZoomState,
	onZoomChange: (Float) -> Unit,
	modifier: Modifier = Modifier, onClosePicker: () -> Unit = {},
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		Surface(
			shape = MaterialTheme.shapes.large,
			color = MaterialTheme.colorScheme.surfaceContainer,
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
					modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
				)
				Slider(
					value = zoomState.zoomRatio(),
					onValueChange = onZoomChange,
					valueRange = zoomState.zoomRange,
					modifier = Modifier.weight(1f)
				)
				Text(
					text = stringResource(
						R.string.zoom_formated_1_decimal_place,
						zoomState.maxZoomRatio
					),
					modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
				)
			}
		}

		FilledTonalIconButton(
			onClick = onClosePicker,
			colors = IconButtonDefaults.filledTonalIconButtonColors(
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer
			)
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
	)
}