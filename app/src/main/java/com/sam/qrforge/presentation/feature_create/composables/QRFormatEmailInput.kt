package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QREmailModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun QRFormatEmailInput(
	modifier: Modifier = Modifier,
	onStateChange: (QRContentModel) -> Unit,
	initialState: QREmailModel = QREmailModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {

	var addressField by rememberSaveable { mutableStateOf(initialState.address) }
	var subjectField by rememberSaveable { mutableStateOf(initialState.subject ?: "") }
	var messageField by rememberSaveable { mutableStateOf(initialState.body ?: "") }

	LaunchedEffect(addressField, subjectField, messageField) {
		snapshotFlow { QREmailModel(addressField, subjectField, messageField) }
			.distinctUntilChanged()
			.debounce(100.milliseconds)
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
			OutlinedTextField(
				value = addressField,
				onValueChange = { value -> addressField = value },
				label = { Text(text = "Email") },
				placeholder = { Text(text = "some@mail.com") },
				shape = shape,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				value = subjectField,
				onValueChange = { value -> subjectField = value },
				label = { Text(text = "Subject") },
				shape = shape,
				minLines = 2,
				singleLine = false,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				value = messageField,
				onValueChange = { value -> messageField = value },
				label = { Text(text = "Message") },
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
private fun QRFormatEmailInputPreview() = QRForgeTheme {
	QRFormatEmailInput(
		onStateChange = {},
		contentPadding = PaddingValues(12.dp)
	)
}