package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_scan.state.SaveResultsDialogState
import com.sam.qrforge.presentation.feature_scan.state.ScanResultScreenEvents
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun SaveResultsDialogContent(
	state: SaveResultsDialogState,
	onEvent: (ScanResultScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	onDismissDialog: () -> Unit = {},
) {
	Box(
		modifier = modifier.sizeIn(
			minWidth = dimensionResource(R.dimen.dialog_min_constraint_width),
			maxWidth = dimensionResource(R.dimen.dialog_min_constraint_height)
		),
		propagateMinConstraints = true,
	) {
		SaveResultsDialogContent(
			value = state.titleText,
			errorMessage = state.errorString ?: "",
			hasError = state.hasError,
			onValueChange = { onEvent(ScanResultScreenEvents.OnUpdateTitle(it)) },
			onSave = { onEvent(ScanResultScreenEvents.OnSaveItem) },
			onCancel = onDismissDialog,
			modifier = modifier,
		)
	}
}


@Composable
private fun SaveResultsDialogContent(
	value: String,
	onValueChange: (String) -> Unit,
	onCancel: () -> Unit,
	modifier: Modifier = Modifier,
	onSave: () -> Unit = {},
	errorMessage: String = "",
	hasError: Boolean = false,
	shape: Shape = AlertDialogDefaults.shape,
	containerColor: Color = AlertDialogDefaults.containerColor,
	tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {

	val focusManager = LocalFocusManager.current

	Surface(
		shape = shape,
		tonalElevation = tonalElevation,
		color = containerColor,
		contentColor = contentColorFor(containerColor),
		modifier = modifier
	) {
		Column(
			modifier = Modifier.padding(24.dp)
		) {
			Text(
				text = stringResource(id = R.string.save_scan_results_dialog_title),
				style = MaterialTheme.typography.headlineMedium,
				color = AlertDialogDefaults.titleContentColor,
				modifier = Modifier.padding(top = 12.dp)
			)
			Text(
				text = stringResource(id = R.string.save_scan_results_dialog_text),
				color = AlertDialogDefaults.textContentColor,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(vertical = 6.dp),
			)
			OutlinedTextField(
				value = value,
				onValueChange = onValueChange,
				label = { Text(text = "Title") },
				placeholder = { Text(text = "Scan Results") },
				isError = hasError,
				supportingText = {
					AnimatedVisibility(
						visible = hasError,
						enter = slideInVertically(),
						exit = slideOutVertically()
					) {
						Text(
							text = errorMessage,
							style = MaterialTheme.typography.labelMedium
						)
					}
				},
				shape = MaterialTheme.shapes.medium,
				keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done
				),
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.align(Alignment.End),
				horizontalArrangement = Arrangement.spacedBy(6.dp)
			) {
				TextButton(
					onClick = onCancel,
					colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
				) {
					Text(
						text = stringResource(id = R.string.dialog_action_cancel),
						style = MaterialTheme.typography.titleMedium
					)
				}
				Button(
					onClick = onSave,
					enabled = !hasError,
				) {
					Text(
						text = stringResource(id = R.string.dialog_action_save),
						style = MaterialTheme.typography.titleMedium
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun SaveResultsDialogContentPreview() = QRForgeTheme {
	SaveResultsDialogContent(
		state = SaveResultsDialogState(),
		onEvent = {},
	)
}