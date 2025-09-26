package com.sam.qrforge.presentation.common.composables

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.domain.models.qr.QRTelephoneModel
import com.sam.qrforge.domain.models.qr.QRWiFiModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRContentStringCard(
	contentString: String,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {

	val scope = rememberCoroutineScope()

	val context = LocalContext.current
	val clipBoard = LocalClipboard.current

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
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.Top,
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(4.dp),
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = stringResource(R.string.select_qr_content_title),
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.secondary,
					)
					Text(
						text = contentString,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
				TooltipBox(
					positionProvider = TooltipDefaults
						.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
					tooltip = {
						PlainTooltip {
							Text(text = stringResource(R.string.action_copy))
						}
					},
					state = rememberTooltipState()
				) {
					FilledTonalIconButton(
						onClick = {
							val clipData = ClipData.newPlainText(
								context.getString(R.string.action_copy),
								contentString
							)
							val clipEntry = ClipEntry(clipData)
							scope.launch { clipBoard.setClipEntry(clipEntry) }
						},
						colors = IconButtonDefaults.filledTonalIconButtonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
							contentColor = MaterialTheme.colorScheme.onSecondary,
						),
						shape = MaterialTheme.shapes.medium,
					) {
						Icon(
							painter = painterResource(R.drawable.ic_copy),
							contentDescription = stringResource(R.string.action_copy),
						)
					}
				}
			}
		}
	}
}

private class QRContentModelPreviewParams : CollectionPreviewParameterProvider<QRContentModel>(
	listOf(
		QRPlainTextModel("Hello world"),
		QRTelephoneModel("0000000000"),
		QRWiFiModel("Some Name Too Long", "Something", isHidden = true)
	)
)


@PreviewLightDark
@Composable
private fun QRContentStringCardPreview(
	@PreviewParameter(QRContentModelPreviewParams::class)
	content: QRContentModel
) = QRForgeTheme {
	QRContentStringCard(contentString = content.toQRString())
}