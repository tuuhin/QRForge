package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRURLModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun QRFormatURLInput(
	modifier: Modifier = Modifier,
	onStateChange: (QRURLModel) -> Unit,
	initialState: QRURLModel = QRURLModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val focusManager = LocalFocusManager.current
	val clipboardManager = LocalClipboard.current

	val scope = rememberCoroutineScope()

	val textFieldState = rememberTextFieldState()

	LaunchedEffect(initialState) {
		// sets the initial text if any on start
		textFieldState.setTextAndPlaceCursorAtEnd(initialState.url)
	}

	LaunchedEffect(textFieldState) {
		// send the updates
		snapshotFlow { textFieldState.text.toString() }
			.collectLatest {
				onStateChange(QRURLModel(it))
			}
	}

	Surface(
		shape = shape,
		color = containerColor,
		contentColor = contentColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				OutlinedButton(
					onClick = { textFieldState.clearText() },
					shape = MaterialTheme.shapes.large,
					modifier = Modifier.weight(1f),
					contentPadding = PaddingValues(vertical = 12.dp),
					colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
				) {
					Icon(
						painter = painterResource(R.drawable.ic_clear),
						contentDescription = stringResource(R.string.action_clear)
					)
					Spacer(modifier = Modifier.width(6.dp))
					Text(text = stringResource(R.string.action_clear))
				}
				Button(
					onClick = {
						scope.launch {
							val entry = clipboardManager.getClipEntry()
							val itemPresent = (entry?.clipData?.itemCount ?: 0) > 0
							if (!itemPresent) return@launch
							val clipboardText = entry?.clipData?.getItemAt(0)?.text ?: return@launch
							textFieldState.edit {
								insert(originalText.length, clipboardText.toString())
								placeCursorAtEnd()
							}
						}
					},
					shape = MaterialTheme.shapes.large,
					contentPadding = PaddingValues(vertical = 12.dp),
					modifier = Modifier.weight(1f),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_paste),
						contentDescription = stringResource(R.string.action_paste)
					)
					Spacer(modifier = Modifier.width(6.dp))
					Text(text = stringResource(R.string.action_paste))
				}
			}
			OutlinedTextField(
				state = textFieldState,
				placeholder = { Text(text = stringResource(R.string.create_qr_fields_url_placeholder)) },
				shape = shape,
				lineLimits = TextFieldLineLimits.MultiLine(2, 2),
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Done,
					keyboardType = KeyboardType.Uri
				),
				onKeyboardAction = KeyboardActionHandler { focusManager.clearFocus() },
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}