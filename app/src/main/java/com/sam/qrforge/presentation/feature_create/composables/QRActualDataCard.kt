package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRActualDataCard(
	content: QRContentModel,
	modifier: Modifier = Modifier
) {

	Card(
		modifier = modifier
			.fillMaxWidth(),
		shape = MaterialTheme.shapes.medium,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
	) {
		Row(
			modifier = Modifier
				.heightIn(80.dp)
				.padding(10.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = "Content",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				Text(
					text = content.toQRString(),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
			}
			Button(
				onClick = {},
				modifier = Modifier.size(36.dp),
				contentPadding = PaddingValues(all = 8.dp),
				shape = MaterialTheme.shapes.medium,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer,
					contentColor = MaterialTheme.colorScheme.onSecondaryContainer
				)
			) {
				Icon(
					painter = painterResource(R.drawable.ic_copy),
					contentDescription = "Action Copy",
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun QRActualDataCardPreview() = QRForgeTheme {
	QRActualDataCard(content = QRPlainTextModel("Hello world"))
}