package com.sam.qrforge.presentation.feature_export.composable

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.hasWriteStoragePermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreenTopAppBar(
	onBeginVerify: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = false,
	navigation: @Composable () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null
) {
	val context = LocalContext.current

	var hasPermission by remember {
		val state = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
			context.hasWriteStoragePermission
		else true
		mutableStateOf(state)
	}

	var showPermissionDialog by remember { mutableStateOf(false) }

	PermissionRequestDialog(
		showDialog = showPermissionDialog,
		onPermissionChange = { isGranted ->
			hasPermission = isGranted
			// if permission granted close the dialog too
			if (hasPermission) showPermissionDialog = false
		},
		onDismissDialog = { showPermissionDialog = false },
	)

	TopAppBar(
		title = { Text(text = stringResource(R.string.qr_editor_title)) },
		actions = {
			OutlinedButton(
				onClick = {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && !hasPermission)
						showPermissionDialog = true
					else onBeginVerify()
				},
				shape = MaterialTheme.shapes.extraLarge,
				colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
				enabled = enabled,
			) {
				Text(
					text = stringResource(R.string.action_verify),
					style = MaterialTheme.typography.titleSmall
				)
			}
			Spacer(modifier = Modifier.width(2.dp))
		},
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		modifier = modifier,
	)
}

@Composable
private fun PermissionRequestDialog(
	showDialog: Boolean,
	onPermissionChange: (Boolean) -> Unit,
	onDismissDialog: () -> Unit,
	modifier: Modifier = Modifier
) {
	if (!showDialog) return

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { isGranted -> onPermissionChange(isGranted) },
	)

	AlertDialog(
		onDismissRequest = onDismissDialog,
		confirmButton = {
			Button(
				onClick = { launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) },
				shape = MaterialTheme.shapes.large,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				)
			) {
				Text(text = stringResource(R.string.dialog_action_allow))
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
		text = { Text(text = stringResource(R.string.allow_permission_external_storage)) },
		modifier = modifier,
	)
}
