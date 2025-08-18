package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRURLModel
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

	var textField by rememberSaveable { mutableStateOf(initialState.url ?: "") }

	LaunchedEffect(textField) {
		val uriModel = QRURLModel(textField)
		snapshotFlow { uriModel }
			.collect { onStateChange(it) }
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
					onClick = { textField = "" },
					shape = MaterialTheme.shapes.large,
					modifier = Modifier.weight(1f),
					contentPadding = PaddingValues(vertical = 12.dp),
					colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
				) {
					Icon(
						painter = painterResource(R.drawable.ic_clear),
						contentDescription = "Clear content"
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(text = "Clear")
				}
				Button(
					onClick = {
						scope.launch {
							val entry = clipboardManager.getClipEntry()
							val itemPresent = (entry?.clipData?.itemCount ?: 0) > 0
							if (itemPresent) {
								val item = entry?.clipData?.getItemAt(0)
								item?.text?.let {
									textField = it.toString()
								}
							}
						}
					},
					shape = MaterialTheme.shapes.large,
					contentPadding = PaddingValues(vertical = 12.dp),
					modifier = Modifier.weight(1f),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_paste),
						contentDescription = "Clear content"
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(text = "Paste")
				}
			}
			OutlinedTextField(
				value = textField,
				onValueChange = { value -> textField = value },
				placeholder = { Text(text = "Your url content") },
				shape = shape,
				maxLines = 2,
				minLines = 2,
				singleLine = false,
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Done,
					keyboardType = KeyboardType.Uri
				),
				keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}