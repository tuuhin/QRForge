package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.sam.qrforge.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermissionDialog(
	showDialog: Boolean,
	status: PermissionStatus,
	onShowLauncher: () -> Unit,
	onShowSettings: () -> Unit,
	onDismissDialog: () -> Unit,
	modifier: Modifier = Modifier
) {
	if (!showDialog) return

	AlertDialog(
		onDismissRequest = onDismissDialog,
		confirmButton = {
			Button(
				onClick = { if (!status.shouldShowRationale) onShowSettings() else onShowLauncher() },
				shape = MaterialTheme.shapes.large,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				)
			) {
				if (!status.shouldShowRationale) Text(stringResource(R.string.settings_options))
				else Text(text = stringResource(R.string.dialog_action_allow))
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismissDialog,
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
			) {
				Text(text = stringResource(R.string.dialog_action_cancel))
			}
		},
		title = { Text(text = stringResource(R.string.allow_permission_title)) },
		text = {
			if (!status.shouldShowRationale) Text(text = stringResource(R.string.storage_permission_denied_text))
			else Text(text = stringResource(R.string.allow_permission_external_storage))
		},
		modifier = modifier,
	)
}
