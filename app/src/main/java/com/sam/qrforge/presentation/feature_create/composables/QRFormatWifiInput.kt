package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

@Composable
fun QRFormatWifiInput(
	modifier: Modifier = Modifier,
	onContentChange: (QRContentModel) -> Unit,
	initialState: QRWiFiModel = QRWiFiModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val focusManager = LocalFocusManager.current
	val focusRequester = remember { FocusRequester() }

	val ssidFieldState = rememberTextFieldState()
	val secretTextField = rememberTextFieldState()
	var securityType by rememberSaveable { mutableStateOf(initialState.encryption) }
	var isHidden by rememberSaveable { mutableStateOf(initialState.isHidden) }

	LaunchedEffect(initialState) {
		ssidFieldState.setTextAndPlaceCursorAtEnd(initialState.ssid ?: "")
		secretTextField.setTextAndPlaceCursorAtEnd(initialState.password ?: "")
	}

	LaunchedEffect(ssidFieldState, secretTextField, securityType, isHidden) {
		val ssidFlow = snapshotFlow { ssidFieldState.text.toString() }
		val securityFlow = snapshotFlow { secretTextField.text.toString() }
		val typeFlow = snapshotFlow { securityType }
		val hiddenFlow = snapshotFlow { isHidden }

		combine(ssidFlow, securityFlow, typeFlow, hiddenFlow) { ssid, secureText, type, hidden ->
			QRWiFiModel(ssid, secureText, type, hidden)
		}.collectLatest { onContentChange(it) }
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
			OutlinedTextField(
				state = ssidFieldState,
				label = { Text(text = stringResource(R.string.create_qr_fields_wifi_ssid)) },
				placeholder = { Text(text = stringResource(R.string.create_qr_fields_wifi_ssid_placeholder)) },
				leadingIcon = {
					Icon(
						painter = painterResource(R.drawable.ic_network),
						contentDescription = "Wifi Network name"
					)
				},
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Next,
					keyboardType = KeyboardType.Ascii
				),
				onKeyboardAction = KeyboardActionHandler { focusRequester.requestFocus() },
				shape = shape,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				state = secretTextField,
				enabled = securityType != QRWiFiModel.WifiEncryption.NO_PASS,
				label = { Text(text = stringResource(R.string.create_qr_fields_wifi_secret)) },
				placeholder = { Text(text = stringResource(R.string.create_qr_fields_wifi_secret_placeholder)) },
				leadingIcon = {
					Icon(
						painter = painterResource(R.drawable.ic_password),
						contentDescription = "Network password"
					)
				},
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Next,
					keyboardType = KeyboardType.Password
				),
				onKeyboardAction = KeyboardActionHandler { focusManager.clearFocus() },
				shape = shape,
				modifier = Modifier
					.focusRequester(focusRequester)
					.fillMaxWidth(),
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 4.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Hidden",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				Switch(
					checked = isHidden,
					onCheckedChange = { isHidden = it },
					colors = SwitchDefaults.colors(
						checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
						checkedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
						checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
					),
				)
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 4.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.create_qr_fields_wifi_security),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				Row(
					modifier = Modifier.wrapContentWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					QRWiFiModel.WifiEncryption.entries.forEach { encryption ->
						InputChip(
							selected = securityType == encryption,
							onClick = {
								securityType = encryption
								if (encryption == QRWiFiModel.WifiEncryption.NO_PASS)
									secretTextField.clearText()
							},
							label = { Text(text = encryption.string) },
							shape = MaterialTheme.shapes.large,
							leadingIcon = {
								AnimatedVisibility(
									visible = securityType == encryption,
									enter = expandIn(),
									exit = shrinkOut()
								) {
									Icon(
										imageVector = Icons.Default.Check,
										contentDescription = "Selected :${encryption.string}"
									)
								}
							},
							colors = InputChipDefaults.inputChipColors(
								selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
								selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
								selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
							)
						)
					}
				}
			}
		}
	}
}

private val QRWiFiModel.WifiEncryption.string: String
	get() = when (this) {
		QRWiFiModel.WifiEncryption.WEP -> "WEP"
		QRWiFiModel.WifiEncryption.WPA -> "WPA"
		QRWiFiModel.WifiEncryption.NO_PASS -> "None"
	}

@PreviewLightDark
@Composable
private fun QRFormatWifiInputPreview() = QRForgeTheme {
	QRFormatWifiInput(
		onContentChange = {},
		contentPadding = PaddingValues(12.dp)
	)
}