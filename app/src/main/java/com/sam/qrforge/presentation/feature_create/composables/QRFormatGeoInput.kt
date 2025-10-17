package com.sam.qrforge.presentation.feature_create.composables

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sam.qrforge.R
import com.sam.qrforge.data.utils.applicationSettingsIntent
import com.sam.qrforge.domain.models.BaseLocationModel
import com.sam.qrforge.domain.models.qr.QRGeoPointModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRFormatGeoInput(
	onStateChange: (QRGeoPointModel) -> Unit,
	modifier: Modifier = Modifier,
	initialState: QRGeoPointModel = QRGeoPointModel(),
	onUseLastKnownLocation: () -> Unit = {},
	shape: Shape = MaterialTheme.shapes.large,
	isLocationEnabled: Boolean = true,
	lastKnownLocation: BaseLocationModel? = null,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
	contentColor: Color = contentColorFor(containerColor),
	contentPadding: PaddingValues = PaddingValues(12.dp),
) {

	val context = LocalContext.current
	val focusManager = LocalFocusManager.current
	val currentOnStateChange by rememberUpdatedState(onStateChange)

	var showDialog by remember { mutableStateOf(false) }

	val permissionState = rememberMultiplePermissionsState(
		permissions = listOf(
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)
	)

	val isLocationPermissionGranted by remember(permissionState) {
		derivedStateOf { permissionState.permissions.any { it.status.isGranted } }
	}

	LocationPermissionDialog(
		showDialog = showDialog,
		isPermanentlyDenied = permissionState.shouldShowRationale,
		onCancel = { showDialog = false },
		onShowLauncher = {
			permissionState.launchMultiplePermissionRequest()
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

	val focusRequester = remember { FocusRequester() }

	val latitudeField = rememberTextFieldState()
	val longitudeField = rememberTextFieldState()

	LaunchedEffect(Unit) {
		latitudeField.setTextAndPlaceCursorAtEnd(initialState.lat.toString())
		longitudeField.setTextAndPlaceCursorAtEnd(initialState.long.toString())
	}

	LaunchedEffect(lastKnownLocation) {
		lastKnownLocation?.let {
			latitudeField.setTextAndPlaceCursorAtEnd(lastKnownLocation.latitude.toString())
			longitudeField.setTextAndPlaceCursorAtEnd(lastKnownLocation.longitude.toString())
		}
	}

	LaunchedEffect(latitudeField, longitudeField) {
		val latFlow = snapshotFlow { latitudeField.text.toString() }
		val longFlow = snapshotFlow { longitudeField.text.toString() }

		combine(latFlow, longFlow) { lat, long ->
			val latAsDouble = lat.toDoubleOrNull() ?: 0.0
			val longAsDouble = long.toDoubleOrNull() ?: 0.0

			val model = QRGeoPointModel(latAsDouble, longAsDouble)
			currentOnStateChange(model)
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
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth()
			) {
				OutlinedButton(
					onClick = {
						latitudeField.clearText()
						longitudeField.clearText()
					},
					shape = MaterialTheme.shapes.large,
					modifier = Modifier.weight(1f),
					contentPadding = PaddingValues(vertical = 12.dp),
					colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
				) {
					Icon(
						painter = painterResource(R.drawable.ic_clear),
						contentDescription = "Clear content"
					)
					Spacer(modifier = Modifier.width(6.dp))
					Text(
						text = stringResource(R.string.action_clear),
						style = MaterialTheme.typography.bodyMedium
					)
				}
				Button(
					onClick = {
						if (isLocationPermissionGranted) onUseLastKnownLocation()
						else showDialog = true
					},
					enabled = isLocationEnabled,
					shape = MaterialTheme.shapes.large,
					contentPadding = PaddingValues(vertical = 12.dp),
					modifier = Modifier.weight(1f)
				) {
					Icon(
						painter = painterResource(R.drawable.ic_geo),
						contentDescription = stringResource(R.string.create_qr_fields_geo_read_location),
						modifier = Modifier.size(20.dp),
					)
					Spacer(modifier = Modifier.width(6.dp))
					Text(
						text = stringResource(R.string.create_qr_fields_geo_read_location),
						style = MaterialTheme.typography.bodyMedium
					)
				}
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.create_qr_fields_geo_lat),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				OutlinedTextField(
					state = latitudeField,
					inputTransformation = InputTransformation.maxLength(8),
					outputTransformation = OutputTransformation {
						val pointIndex = this.originalText.indexOfFirst { it == '.' }
						if (pointIndex == -1 || pointIndex > originalText.length)
							return@OutputTransformation

						addStyle(
							SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
							start = 0,
							end = pointIndex
						)
						addStyle(
							SpanStyle(fontSize = 14.sp),
							start = pointIndex + 1,
							end = originalText.length
						)
					},
					placeholder = { Text(text = "0.0") },
					shape = shape,
					keyboardOptions = KeyboardOptions(
						imeAction = ImeAction.Next,
						keyboardType = KeyboardType.Decimal
					),
					onKeyboardAction = KeyboardActionHandler { focusRequester.requestFocus() },
					lineLimits = TextFieldLineLimits.SingleLine,
					modifier = Modifier.width(120.dp),
				)
			}
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.create_qr_fields_geo_long),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				OutlinedTextField(
					state = longitudeField,
					placeholder = { Text(text = "0.0") },
					inputTransformation = InputTransformation.maxLength(8),
					outputTransformation = OutputTransformation {
						val pointIndex = this.originalText.indexOfFirst { it == '.' }
						if (pointIndex == -1 || pointIndex > originalText.length)
							return@OutputTransformation

						addStyle(
							SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
							start = 0,
							end = pointIndex
						)
						addStyle(
							SpanStyle(fontSize = 14.sp),
							start = pointIndex + 1,
							end = originalText.length
						)
					},
					shape = shape,
					keyboardOptions = KeyboardOptions(
						imeAction = ImeAction.Done,
						keyboardType = KeyboardType.Decimal
					),
					onKeyboardAction = KeyboardActionHandler { focusManager.clearFocus() },
					lineLimits = TextFieldLineLimits.SingleLine,
					modifier = Modifier
						.focusRequester(focusRequester)
						.width(120.dp),
				)
			}
			HorizontalDivider()
			Text(
				text = stringResource(R.string.create_qr_fields_geo_extra_text),
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.tertiary,
				modifier = Modifier.align(Alignment.CenterHorizontally),
				textAlign = TextAlign.Center,
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun QRFormatGeoInputPreview() = QRForgeTheme {
	Surface {
		QRFormatGeoInput(
			onStateChange = {},
			contentPadding = PaddingValues(12.dp)
		)
	}
}