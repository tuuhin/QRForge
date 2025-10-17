package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingLocationDialog(
	showDialog: Boolean,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = AlertDialogDefaults.shape,
	containerColor: Color = AlertDialogDefaults.containerColor,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {

	if (!showDialog) return

	BasicAlertDialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(dismissOnClickOutside = false),
		modifier = modifier
	) {
		Surface(
			shape = shape,
			tonalElevation = tonalElevation,
			color = containerColor,
			contentColor = contentColorFor(containerColor),
		) {
			Column(
				modifier = Modifier
					.padding(24.dp)
					.widthIn(min = dimensionResource(R.dimen.dialog_min_constraint_width)),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(
					text = stringResource(R.string.read_current_location_dialog_title),
					style = MaterialTheme.typography.headlineSmall,
					color = AlertDialogDefaults.titleContentColor,
					textAlign = TextAlign.Center,
					modifier = Modifier.padding(bottom = 4.dp)
				)
				Text(
					text = stringResource(R.string.read_current_location_dialog_text),
					color = AlertDialogDefaults.textContentColor,
					style = MaterialTheme.typography.bodyMedium,
					textAlign = TextAlign.Center,
					modifier = Modifier.padding(horizontal = 8.dp)
				)
				CircularProgressIndicator(
					strokeWidth = 3.dp,
					strokeCap = StrokeCap.Round,
					modifier = Modifier.size(42.dp)
				)
				Spacer(modifier = Modifier.height(12.dp))
				Button(
					onClick = onDismiss,
					shape = MaterialTheme.shapes.extraLarge
				) {
					Text(text = stringResource(R.string.dialog_action_cancel))
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun ReadingLocationDialogPreview() = QRForgeTheme {
	ReadingLocationDialog(showDialog = true, onDismiss = {})
}