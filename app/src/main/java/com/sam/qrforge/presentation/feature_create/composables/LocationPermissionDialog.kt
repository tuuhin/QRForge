package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun LocationPermissionDialog(
	showDialog: Boolean,
	isPermanentlyDenied: Boolean,
	onOpenSettings: () -> Unit,
	onShowLauncher: () -> Unit,
	onCancel: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = AlertDialogDefaults.shape,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
	containerColor: Color = AlertDialogDefaults.containerColor
) {
	if (!showDialog) return

	AlertDialog(
		onDismissRequest = onCancel,
		confirmButton = {
			Button(
				onClick = {
					if (isPermanentlyDenied) onOpenSettings()
					else onShowLauncher()
				},
				shape = MaterialTheme.shapes.large,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondary,
					contentColor = MaterialTheme.colorScheme.onSecondary
				)
			) {
				if (isPermanentlyDenied)
					Text(stringResource(R.string.settings_options))
				else Text(stringResource(R.string.allow_permission_text))
			}
		},
		dismissButton = {
			TextButton(
				onClick = onCancel,
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
			) {
				Text(
					text = stringResource(R.string.dialog_action_cancel),
					style = MaterialTheme.typography.titleSmall
				)
			}
		},
		title = { Text(stringResource(R.string.location_permission_title)) },
		text = {
			if (isPermanentlyDenied)
				Text(stringResource(R.string.permission_denied_text))
			else Text(stringResource(R.string.location_permission_text))
		},
		icon = {
			Icon(
				painter = painterResource(R.drawable.ic_geo),
				contentDescription = "Location",
				modifier = Modifier.padding(16.dp)
			)
		},
		modifier = modifier,
		shape = shape,
		containerColor = containerColor,
		tonalElevation = tonalElevation
	)
}

@PreviewLightDark
@Composable
private fun ReadLocationPermissionDialogPreview() = QRForgeTheme {
	LocationPermissionDialog(
		showDialog = true,
		isPermanentlyDenied = false,
		onCancel = {},
		onShowLauncher = {},
		onOpenSettings = {})
}