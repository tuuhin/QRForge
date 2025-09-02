package com.sam.qrforge.presentation.common.composables

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
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRContentStringCard(
	contentString: String,
	onContentCopy: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
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
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
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
						overflow = TextOverflow.Ellipsis
					)
				}
				TooltipBox(
					positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
					tooltip = {
						PlainTooltip {
							Text(text = stringResource(R.string.action_copy))
						}
					},
					state = rememberTooltipState()
				) {
					FilledTonalIconButton(
						onClick = onContentCopy,
						colors = IconButtonDefaults.filledTonalIconButtonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
							contentColor = MaterialTheme.colorScheme.onSecondary,
						),
						shape = MaterialTheme.shapes.large,
					) {
						Icon(
							painter = painterResource(R.drawable.ic_copy),
							contentDescription = "Copy button",
						)
					}
				}
			}
		}
	}
}