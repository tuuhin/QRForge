package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRSmsModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun QRFormatSMSInput(
	onStateChange: (QRSmsModel) -> Unit,
	modifier: Modifier = Modifier,
	initialState: QRSmsModel = QRSmsModel(),
	onOpenContacts: () -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val focusManager = LocalFocusManager.current
	val clipboardManager = LocalClipboard.current

	val scope = rememberCoroutineScope()

	val focusRequester = remember { FocusRequester() }

	var phNumber by rememberSaveable(initialState.phoneNumber) {
		mutableStateOf(initialState.phoneNumber ?: "")
	}
	var message by rememberSaveable { mutableStateOf(initialState.message ?: "") }

	LaunchedEffect(phNumber, message) {
		val model = QRSmsModel(phoneNumber = phNumber, message = message)
		snapshotFlow { model }
			.collectLatest { onStateChange(it) }
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
					value = phNumber,
					onValueChange = { value -> phNumber = value },
					placeholder = { Text(text = "Phone") },
					leadingIcon = {
						Icon(
							painter = painterResource(R.drawable.ic_phone),
							contentDescription = "Phone"
						)
					},
					label = { Text(text = "Phone") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number,
						imeAction = ImeAction.Next
					),
					keyboardActions = KeyboardActions(
						onNext = { focusRequester.requestFocus() },
					), shape = shape,
					maxLines = 1,
					singleLine = true,
					modifier = Modifier.weight(1f)
				)
				Surface(
					onClick = onOpenContacts,
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
				value = message,
				onValueChange = { value -> message = value },
				placeholder = { Text(text = "Hey how are you") },
				label = { Text(text = "Message") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
				shape = shape,
				minLines = 3,
				maxLines = 5,
				singleLine = false,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(focusRequester),
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier.align(Alignment.End)
			) {
				SuggestionChip(
					onClick = { message = "" },
					icon = {
						Icon(
							painter = painterResource(R.drawable.ic_clear),
							contentDescription = "Clear content"
						)
					},
					shape = MaterialTheme.shapes.large,
					label = { Text(text = "Clear") },
				)
				SuggestionChip(
					onClick = {
						scope.launch {
							val entry = clipboardManager.getClipEntry()
							val itemPresent = (entry?.clipData?.itemCount ?: 0) > 0
							if (!itemPresent) return@launch

							val item = entry?.clipData?.getItemAt(0) ?: return@launch
							message = item.text.toString()
						}
					},
					icon = {
						Icon(
							painter = painterResource(R.drawable.ic_paste),
							contentDescription = "Clear content"
						)
					},
					shape = MaterialTheme.shapes.large,
					label = { Text("Paste Message") },
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