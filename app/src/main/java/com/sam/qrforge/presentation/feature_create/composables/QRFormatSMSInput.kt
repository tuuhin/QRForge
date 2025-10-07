package com.sam.qrforge.presentation.feature_create.composables

import android.Manifest
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sam.qrforge.R
import com.sam.qrforge.data.contracts.PickContactsContract
import com.sam.qrforge.data.utils.applicationSettingsIntent
import com.sam.qrforge.domain.models.ContactsDataModel
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRFormatSMSInput(
	onStateChange: (QRSmsModel) -> Unit,
	modifier: Modifier = Modifier,
	initialState: QRSmsModel = QRSmsModel(),
	readContactsModel: ContactsDataModel? = null,
	onSelectContacts: (String) -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val context = LocalContext.current

	val focusManager = LocalFocusManager.current
	val clipboardManager = LocalClipboard.current

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
			} catch (_: ActivityNotFoundException) {
			}
			showDialog = false
		},
	)

	val scope = rememberCoroutineScope()
	val focusRequester = remember { FocusRequester() }

	val phNumberFieldState = rememberTextFieldState()
	val messageTextFieldState = rememberTextFieldState()

	LaunchedEffect(Unit) {
		phNumberFieldState.setTextAndPlaceCursorAtEnd(initialState.phoneNumber ?: "")
		messageTextFieldState.setTextAndPlaceCursorAtEnd(initialState.message ?: "")
	}

	LaunchedEffect(readContactsModel) {
		readContactsModel?.let {
			phNumberFieldState.setTextAndPlaceCursorAtEnd(it.phoneNumber)
		}
	}

	LaunchedEffect(phNumberFieldState, messageTextFieldState) {
		val phNumber = snapshotFlow { phNumberFieldState.text.toString() }
		val messages = snapshotFlow { messageTextFieldState.text.toString() }

		combine(phNumber, messages) { number, message ->
			onStateChange(QRSmsModel(phoneNumber = number, message = message))
		}.launchIn(this)
	}

	Surface(
		shape = shape,
		color = containerColor,
		contentColor = contentColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(
					state = phNumberFieldState,
					inputTransformation = InputTransformation.maxLength(15),
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
						imeAction = ImeAction.Next
					),
					onKeyboardAction = KeyboardActionHandler {
						focusRequester.requestFocus()
					},
					shape = shape,
					lineLimits = TextFieldLineLimits.SingleLine,
					modifier = Modifier.weight(1f)
				)
				Surface(
					onClick = {
						if (permissionState.status.isGranted)
							readContactsLauncher.launch(Unit)
						else showDialog = true
					},
					shape = MaterialTheme.shapes.large,
					color = MaterialTheme.colorScheme.primary,
				) {
					Icon(
						painter = painterResource(R.drawable.ic_phonebook),
						contentDescription = "Phone book",
						modifier = Modifier.padding(16.dp)
					)
				}
			}
			OutlinedTextField(
				state = messageTextFieldState,
				placeholder = { Text(text = stringResource(R.string.create_qr_fields_sms_placeholder)) },
				label = { Text(text = stringResource(R.string.create_qr_fields_sms_message)) },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done
				),
				onKeyboardAction = KeyboardActionHandler {
					focusManager.clearFocus()
				},
				shape = shape,
				lineLimits = TextFieldLineLimits.MultiLine(
					minHeightInLines = 3,
					maxHeightInLines = 5
				),
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(focusRequester),
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.align(Alignment.End)
			) {
				SuggestionChip(
					onClick = { messageTextFieldState.clearText() },
					icon = {
						Icon(
							painter = painterResource(R.drawable.ic_clear),
							contentDescription = stringResource(R.string.action_clear)
						)
					},
					shape = MaterialTheme.shapes.large,
					label = { Text(text = stringResource(R.string.action_clear)) },
				)
				SuggestionChip(
					onClick = {
						scope.launch {
							val entry = clipboardManager.getClipEntry()
							val itemPresent = (entry?.clipData?.itemCount ?: 0) > 0
							if (!itemPresent) return@launch

							val item = entry?.clipData?.getItemAt(0) ?: return@launch
							val clipboardText = item.text.toString()
							messageTextFieldState.edit {
								insert(originalText.length, clipboardText)
								placeCursorAtEnd()
							}
						}
					},
					icon = {
						Icon(
							painter = painterResource(R.drawable.ic_paste),
							contentDescription = stringResource(R.string.action_paste)
						)
					},
					shape = MaterialTheme.shapes.large,
					label = { Text(text = stringResource(R.string.action_paste)) },
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun QRFormatSMSInputPreview() = QRForgeTheme {
	QRFormatSMSInput(
		onStateChange = {},
	)
}