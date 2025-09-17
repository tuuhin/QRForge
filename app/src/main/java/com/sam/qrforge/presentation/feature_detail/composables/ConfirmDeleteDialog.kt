package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun ConfirmDeleteDialog(
	showDialog: Boolean,
	modifier: Modifier = Modifier,
	onDismiss: () -> Unit = {},
	canDelete: Boolean = true,
	onConfirmDelete: () -> Unit = {},
	shape: Shape = AlertDialogDefaults.shape,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
	if (!showDialog) return

	AlertDialog(
		onDismissRequest = onDismiss,
		modifier = modifier,
		confirmButton = {
			Button(
				onClick = onConfirmDelete,
				enabled = canDelete,
				colors = ButtonDefaults.textButtonColors(
					contentColor = MaterialTheme.colorScheme.onErrorContainer,
					containerColor = MaterialTheme.colorScheme.errorContainer
				)
			) {
				Text(text = stringResource(R.string.dialog_action_delete))
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
		icon = {
			Icon(
				painter = painterResource(R.drawable.ic_warning),
				contentDescription = "Delete"
			)
		},
		title = { Text(text = stringResource(R.string.delete_qr_dialog_title)) },
		text = { Text(text = stringResource(R.string.delete_qr_dialog_text)) },
		shape = shape,
		tonalElevation = tonalElevation,
	)
}

@PreviewLightDark
@Composable
private fun ConfirmDeleteDialogPreview() = QRForgeTheme {
	ConfirmDeleteDialog(showDialog = true)
}