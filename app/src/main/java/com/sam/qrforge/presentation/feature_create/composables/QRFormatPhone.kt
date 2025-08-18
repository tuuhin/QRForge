package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QRFormatPhoneInput(
	modifier: Modifier = Modifier,
	onSelectContacts: () -> Unit = {},
	onStateChange: (QRContentModel) -> Unit,
	initialState: QRTelephoneModel = QRTelephoneModel(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = contentColorFor(containerColor),
) {

	var phNumber by rememberSaveable(initialState.number) {
		mutableStateOf(initialState.number ?: "")
	}

	LaunchedEffect(phNumber) {
		val model = QRTelephoneModel(phNumber)

		snapshotFlow { model }
			.collectLatest { onStateChange(model) }
	}

	Surface(
		shape = shape,
		color = containerColor,
		contentColor = contentColor,
		modifier = modifier,
	) {
		Column(
			modifier = modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = "Enter the telecom number of the peer or select from your contacts",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(
					value = phNumber,
					onValueChange = { value -> phNumber = value },
					placeholder = { Text(text = "Phone") },
					label = { Text(text = "Phone") },
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
					shape = shape,
					maxLines = 1,
					singleLine = true,
					modifier = Modifier.weight(1f)
				)
				Surface(
					onClick = onSelectContacts,
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
		}
	}
}

@Preview
@Composable
private fun QRFormatPhoneInputPreview() = QRForgeTheme {
	QRFormatPhoneInput(onStateChange = {})
}