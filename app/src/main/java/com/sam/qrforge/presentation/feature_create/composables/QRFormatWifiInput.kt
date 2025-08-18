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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
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

	var ssidField by rememberSaveable { mutableStateOf(initialState.ssid ?: "") }
	var secretTextField by rememberSaveable { mutableStateOf(initialState.password ?: "") }
	var securityType by rememberSaveable { mutableStateOf(initialState.encryption) }
	var isHidden by rememberSaveable { mutableStateOf(initialState.isHidden) }

	val currentOnContentChange by rememberUpdatedState(onContentChange)

	LaunchedEffect(ssidField, secretTextField, securityType, isHidden) {
		val model = QRWiFiModel(
			ssid = ssidField,
			password = if (securityType == QRWiFiModel.WifiEncryption.NO_PASS) null else secretTextField,
			encryption = securityType,
			isHidden = isHidden,
		)
		snapshotFlow { model }
			.distinctUntilChanged()
			.debounce(100.milliseconds)
			.collectLatest { currentOnContentChange(it) }
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
				value = ssidField,
				onValueChange = { value -> ssidField = value },
				label = { Text(text = "Network") },
				placeholder = { Text(text = "My home network") },
				leadingIcon = {
					Icon(
						painter = painterResource(R.drawable.ic_network),
						contentDescription = "Wifi Network name"
					)
				},
				shape = shape,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				value = secretTextField,
				onValueChange = { value -> secretTextField = value },
				enabled = securityType != QRWiFiModel.WifiEncryption.NO_PASS,
				label = { Text(text = "Password") },
				placeholder = { Text(text = "Wifi Password") },
				leadingIcon = {
					Icon(
						painter = painterResource(R.drawable.ic_password),
						contentDescription = "Network password"
					)
				},
				shape = shape,
				modifier = Modifier.fillMaxWidth(),
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
					)
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
					text = "Security",
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
									secretTextField = ""
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