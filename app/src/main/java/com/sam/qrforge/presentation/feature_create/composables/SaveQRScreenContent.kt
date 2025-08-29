package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenEvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenState

@Composable
fun SaveQRScreenContent(
	state: SaveQRScreenState,
	onEvent: (SaveQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues()
) {

	val focusManager = LocalFocusManager.current
	val focusRequester = remember { FocusRequester() }

	Column(
		modifier = modifier
			.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = stringResource(R.string.save_qr_screen_info_text),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		OutlinedTextField(
			value = state.title,
			onValueChange = { value -> onEvent(SaveQRScreenEvents.OnSaveQRTitleChange(value)) },
			label = { Text(text = "Title") },
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Text,
				imeAction = ImeAction.Next
			),
			keyboardActions = KeyboardActions(
				onNext = { focusRequester.requestFocus(FocusDirection.Down) },
			),
			isError = state.isError,
			placeholder = { Text(text = "My QR") },
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.fillMaxWidth(),
		)
		if (state.isError) {
			Text(
				text = "Cannot have empty title",
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.labelMedium
			)
		}
		OutlinedTextField(
			value = state.desc,
			onValueChange = { value -> onEvent(SaveQRScreenEvents.OnSaveQRDescChange(value)) },
			label = { Text(text = "Description") },
			placeholder = { Text(text = "Optional description") },
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Text,
				imeAction = ImeAction.Done
			),
			keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
			shape = MaterialTheme.shapes.medium,
			minLines = 2,
			singleLine = false,
			modifier = Modifier
				.focusRequester(focusRequester)
				.fillMaxWidth(),
		)
	}
}