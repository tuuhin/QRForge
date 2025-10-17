package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

@OptIn(FlowPreview::class)
@Composable
fun QRFormatEmailInput(
	onStateChange: (QRContentModel) -> Unit,
	modifier: Modifier = Modifier,
	initialState: QREmailModel = QREmailModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {
	val focusManager = LocalFocusManager.current

	val focusRequester1 = remember { FocusRequester() }
	val focusRequester2 = remember { FocusRequester() }

	val emailAddressFieldState = rememberTextFieldState()
	val subjectFieldState = rememberTextFieldState()
	val messageFieldState = rememberTextFieldState()

	LaunchedEffect(initialState) {
		emailAddressFieldState.setTextAndPlaceCursorAtEnd(initialState.address)
		subjectFieldState.setTextAndPlaceCursorAtEnd(initialState.subject ?: "")
		messageFieldState.setTextAndPlaceCursorAtEnd(initialState.body ?: "")
	}

	LaunchedEffect(emailAddressFieldState, subjectFieldState, messageFieldState) {
		val email = snapshotFlow { emailAddressFieldState.text.toString() }
		val subject = snapshotFlow { subjectFieldState.text.toString() }
		val message = snapshotFlow { messageFieldState.text.toString() }

		combine(email, subject, message) { email, subject, message ->
			onStateChange(QREmailModel(address = email, subject = subject, body = message))
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
			OutlinedTextField(
				state = emailAddressFieldState,
				label = { Text(text = stringResource(R.string.create_qr_fields_email_email)) },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Email,
					imeAction = ImeAction.Next
				),
				lineLimits = TextFieldLineLimits.SingleLine,
				onKeyboardAction = KeyboardActionHandler {
					focusRequester1.requestFocus(FocusDirection.Down)
				},
				placeholder = { Text(text = stringResource(R.string.create_qr_fields_email_extra_email)) },
				shape = shape,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				state = subjectFieldState,
				label = { Text(text = stringResource(R.string.create_qr_fields_email_subject)) },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Next
				),
				lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 2),
				onKeyboardAction = KeyboardActionHandler {
					focusRequester2.requestFocus(FocusDirection.Down)
				},
				shape = shape,
				modifier = Modifier
					.focusRequester(focusRequester1)
					.fillMaxWidth(),
			)
			OutlinedTextField(
				state = messageFieldState,
				label = { Text(text = stringResource(R.string.create_qr_fields_email_message)) },
				shape = shape,
				lineLimits = TextFieldLineLimits.MultiLine(
					maxHeightInLines = 10,
					minHeightInLines = 4
				),
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Done
				),
				onKeyboardAction = KeyboardActionHandler { focusManager.clearFocus() },
				modifier = Modifier
					.focusRequester(focusRequester2)
					.fillMaxWidth(),
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun QRFormatEmailInputPreview() = QRForgeTheme {
	QRFormatEmailInput(
		onStateChange = {},
		contentPadding = PaddingValues(12.dp)
	)
}