package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.presentation.common.composables.painter
import com.sam.qrforge.presentation.common.composables.toActionText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRActions(
	hasAssociatedAction: Boolean,
	onAction: () -> Unit,
	onShare: () -> Unit,
	modifier: Modifier = Modifier,
	type: QRDataType = QRDataType.TYPE_TEXT,
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		TooltipBox(
			positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
				TooltipAnchorPosition.Above
			),
			tooltip = {
				PlainTooltip {
					Text(text = stringResource(R.string.action_share))
				}
			},
			state = rememberTooltipState()
		) {
			SmallFloatingActionButton(
				onClick = onShare,
				elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
				shape = MaterialTheme.shapes.extraLarge,
				containerColor = MaterialTheme.colorScheme.tertiaryContainer,
				contentColor = MaterialTheme.colorScheme.onTertiaryContainer
			) {
				Icon(
					painter = painterResource(R.drawable.ic_share),
					contentDescription = "Share icon"
				)
			}
		}
		if (hasAssociatedAction) {
			Button(
				onClick = onAction,
				shape = MaterialTheme.shapes.extraLarge,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					contentColor = MaterialTheme.colorScheme.onPrimaryContainer
				),
				contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
			) {
				Icon(painter = type.painter, contentDescription = "Action")
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					text = type.toActionText ?: "",
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}