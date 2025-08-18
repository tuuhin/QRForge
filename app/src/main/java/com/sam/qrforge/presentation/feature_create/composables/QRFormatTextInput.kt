package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun QRFormatTextInput(
	modifier: Modifier = Modifier,
	onStateChange: (QRPlainTextModel) -> Unit,
	isPlainText: Boolean = true,
	initialState: QRPlainTextModel = QRPlainTextModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {

	val clipboardManager = LocalClipboard.current
	val scope = rememberCoroutineScope()

	var textField by rememberSaveable { mutableStateOf(initialState.text) }

	LaunchedEffect(textField) {
		snapshotFlow { textField }
			.distinctUntilChanged { old, new -> old == new }
			.debounce(100.milliseconds)
			.map { text -> QRPlainTextModel(text) }
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
				placeholder = { Text(text = "QR text content") },
				shape = shape,
				minLines = 4,
				maxLines = 10,
				singleLine = false,
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun QRFormatTextInputPreview() = QRForgeTheme {
	QRFormatTextInput(
		onStateChange = {},
		contentPadding = PaddingValues(12.dp)
	)
}