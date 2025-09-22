package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.common.mappers.localeString
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun ExportFormatSelectorDialog(
	showDialog: Boolean,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
	modifier: Modifier = Modifier,
	selectedExportType: ImageMimeTypes = ImageMimeTypes.PNG,
	onExportTypeChange: (ImageMimeTypes) -> Unit = {},
	isExportRunning: Boolean = false,
	shape: Shape = AlertDialogDefaults.shape,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
	containerColor: Color = AlertDialogDefaults.containerColor,
) {
	if (!showDialog) return

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = onConfirm,
				enabled = !isExportRunning,
				colors = ButtonDefaults.buttonColors(
					contentColor = MaterialTheme.colorScheme.primary,
					containerColor = MaterialTheme.colorScheme.onPrimary
				)
			) {
				Text(stringResource(R.string.action_export))
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss,
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
			) {
				Text(text = stringResource(R.string.dialog_action_cancel))
			}
		},
		title = { Text(text = stringResource(R.string.export_qr_dialog_title)) },
		text = {
			Column {
				Text(
					text = stringResource(R.string.export_select_image_format_title),
					style = MaterialTheme.typography.bodyLarge
				)
				Spacer(modifier = Modifier.height(2.dp))
				ImageMimeTypes.entries.forEach { type ->
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.fillMaxWidth(),
					) {
						RadioButton(
							selected = type == selectedExportType,
							onClick = { onExportTypeChange(type) },
						)
						Text(
							text = type.localeString,
							style = MaterialTheme.typography.labelLarge,
							modifier = Modifier.weight(1f)
						)
					}
				}
			}
		},
		modifier = modifier,
		shape = shape,
		tonalElevation = tonalElevation,
		containerColor = containerColor
	)
}

@PreviewLightDark
@Composable
private fun ExportFormatSelectorDialogPreview() = QRForgeTheme {
	ExportFormatSelectorDialog(
		showDialog = true,
		onConfirm = {},
		onDismiss = {},
	)
}