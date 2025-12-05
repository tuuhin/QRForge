package com.sam.qrforge.presentation.feature_create.composables

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sam.qrforge.R
import com.sam.qrforge.data.contracts.PickContactsContract
import com.sam.qrforge.data.utils.applicationSettingsIntent
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRFormatPhoneInput(
	modifier: Modifier = Modifier,
	onSelectContacts: (String) -> Unit = {},
	onStateChange: (QRContentModel) -> Unit,
	readContactsModel: ContactsDataModel? = null,
	initialState: QRTelephoneModel = QRTelephoneModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val context = LocalContext.current
	val focusManager = LocalFocusManager.current

	var showDialog by remember { mutableStateOf(false) }

	val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)

	val currentOnSelectContracts by rememberUpdatedState(onSelectContacts)
	val readContactsLauncher = rememberLauncherForActivityResult(
		contract = PickContactsContract(),
		onResult = { uri ->
			val uriString = uri?.toString() ?: return@rememberLauncherForActivityResult
			currentOnSelectContracts(uriString)
		},
	)

	ContactsPermissionDialog(
		showDialog = showDialog,
		permissionStatus = permissionState.status,
		onCancel = { showDialog = false },
		onShowLauncher = {
			permissionState.launchPermissionRequest()
			showDialog = false
		},
		onOpenSettings = {
			try {
				context.startActivity(context.applicationSettingsIntent)
			} finally {
				showDialog = false
			}
		},
	)

	val phNumberFieldState = rememberTextFieldState()

	// updates the value initially only one time
	LaunchedEffect(Unit) {
		phNumberFieldState.setTextAndPlaceCursorAtEnd(initialState.number ?: "")
	}

	// updates the value when contact info is read
	LaunchedEffect(readContactsModel) {
		readContactsModel?.let {
			phNumberFieldState.setTextAndPlaceCursorAtEnd(it.phoneNumber)
		}
	}

	LaunchedEffect(Unit) {
		snapshotFlow { phNumberFieldState.text.toString() }
			.collectLatest { number -> onStateChange(QRTelephoneModel(number)) }
	}

	Surface(
		shape = shape,
		color = containerColor,
		contentColor = contentColor,
		modifier = modifier,
	) {
		Column(
			modifier = modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = stringResource(R.string.create_qr_fields_phone_extra),
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.tertiary
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(
					state = phNumberFieldState,
					placeholder = { Text(text = stringResource(R.string.create_qr_fields_phone)) },
					leadingIcon = {
						Icon(
							painter = painterResource(R.drawable.ic_phone),
							contentDescription = "Phone"
						)
					},
					label = { Text(text = stringResource(R.string.create_qr_fields_phone)) },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number,
						imeAction = ImeAction.Done
					),
					onKeyboardAction = KeyboardActionHandler {
						focusManager.clearFocus()
					},
					shape = shape,
					lineLimits = TextFieldLineLimits.SingleLine,
					modifier = Modifier.weight(1f)
				)
				FilledTonalButton(
					onClick = {
						if (permissionState.status.isGranted)
							readContactsLauncher.launch(Unit)
						else showDialog = true
					},
					shape = MaterialTheme.shapes.large,
					colors = ButtonDefaults.filledTonalButtonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary
					),
					contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_phonebook),
						contentDescription = "Phone book",
					)
				}
			}
		}
	}
}


@Preview
@Composable
private fun QRFormatPhoneInputPreview() = QRForgeTheme {
	QRFormatPhoneInput(onStateChange = {})
}