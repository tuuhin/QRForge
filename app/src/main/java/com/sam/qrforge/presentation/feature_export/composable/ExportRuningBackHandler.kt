package com.sam.qrforge.presentation.feature_export.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun ExportRunningBackHandler(
	isExportRunning: Boolean,
	modifier: Modifier = Modifier,
	onCancelExport: (() -> Unit)? = null,
) {

	var showDialog by remember { mutableStateOf(false) }

	BackHandler(enabled = isExportRunning, onBack = { showDialog = true })

	ExportRunningWarningDialog(
		showDialog = showDialog,
		onDismissDialog = { showDialog = false },
		onCancelExport = onCancelExport,
		modifier = modifier,
	)

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExportRunningWarningDialog(
	showDialog: Boolean,
	onDismissDialog: () -> Unit,
	modifier: Modifier = Modifier,
	onCancelExport: (() -> Unit)? = null,
	shape: Shape = AlertDialogDefaults.shape,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
	containerColor: Color = AlertDialogDefaults.containerColor,
) {
	if (!showDialog) return

	BasicAlertDialog(
		onDismissRequest = onDismissDialog,
		modifier = modifier
	) {
		Surface(
			shape = shape,
			tonalElevation = tonalElevation,
			color = containerColor,
			contentColor = contentColorFor(containerColor),
			modifier = modifier
		) {
			Column(
				modifier = Modifier.padding(24.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Icon(
					painter = painterResource(R.drawable.ic_export),
					contentDescription = "Export",
					modifier = Modifier.size(28.dp)
				)
				Text(
					text = stringResource(R.string.export_running_dialog_title),
					style = MaterialTheme.typography.headlineMedium,
					color = AlertDialogDefaults.titleContentColor,
					modifier = Modifier.padding(top = 12.dp)
				)
				Text(
					text = stringResource(R.string.export_running_dialog_desc),
					color = AlertDialogDefaults.textContentColor,
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(vertical = 6.dp),
				)
				Spacer(modifier = Modifier.height(8.dp))
				onCancelExport?.let { lambda ->
					Button(
						onClick = lambda,
						colors = ButtonDefaults.textButtonColors(
							contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
							containerColor = MaterialTheme.colorScheme.secondaryContainer
						)
					) {
						Text(
							text = stringResource(id = R.string.dialog_action_cancel),
							style = MaterialTheme.typography.titleMedium
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun ExportRunningWarningDialogPreview() = QRForgeTheme {
	ExportRunningWarningDialog(
		showDialog = true,
		onDismissDialog = {},
	)
}