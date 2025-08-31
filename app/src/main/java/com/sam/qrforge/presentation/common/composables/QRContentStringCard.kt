package com.sam.qrforge.presentation.common.composables

import android.content.ClipData
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import kotlinx.coroutines.launch

@Composable
fun QRContentStringCard(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
	contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {

	val clipboard = LocalClipboard.current
	val scope = rememberCoroutineScope()

	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier
				.heightIn(80.dp)
				.padding(contentPadding),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = stringResource(R.string.select_qr_content_title),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
				Text(
					text = content.toQRString(),
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
			}
			Button(
				onClick = {
					scope.launch {
						val clipEntry = ClipEntry(
							ClipData.newPlainText(
								"QR content", content.toQRString()
							)
						)
						clipboard.setClipEntry(clipEntry)
					}
				},
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


@Composable
fun QRContentStringCard(
	contentString: String,
	onContentCopy: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
	contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier.fillMaxWidth(),
	) {
		Row(
			modifier = Modifier
				.heightIn(min = 80.dp)
				.padding(contentPadding),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.select_qr_content_title),
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.secondary,
						fontWeight = FontWeight.SemiBold
					)
					IconButton(
						onClick = onContentCopy,
						colors = IconButtonDefaults.iconButtonColors(
							containerColor = MaterialTheme.colorScheme.tertiary,
							contentColor = MaterialTheme.colorScheme.onTertiary,
						),
					) {
						Icon(
							painter = painterResource(R.drawable.ic_copy),
							contentDescription = "Copy button",
						)
					}
				}
				Text(
					text = contentString,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}