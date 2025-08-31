package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCommonActions(
	showActions: Boolean,
	onShare: () -> Unit,
	onExport: () -> Unit,
	onAction: () -> Unit,
	modifier: Modifier = Modifier,
	type: QRDataType = QRDataType.TYPE_TEXT,
) {
	AnimatedVisibility(
		visible = showActions,
		enter = slideInVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + expandVertically(expandFrom = Alignment.Bottom),
		exit = slideOutVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + shrinkVertically(shrinkTowards = Alignment.Bottom),
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			TooltipBox(
				positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
				tooltip = {
					PlainTooltip {
						Text(text = stringResource(R.string.action_share))
					}
				},
				state = rememberTooltipState()
			) {
				FloatingActionButton(
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
			ExtendedFloatingActionButton(
				onClick = onAction,
				elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
				shape = MaterialTheme.shapes.extraLarge,
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
				icon = {
					Icon(painter = type.painter, contentDescription = "Action")
				},
				text = {
					Text(
						text = type.toActionText,
						style = MaterialTheme.typography.titleMedium
					)
				},
			)
			TooltipBox(
				positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
				tooltip = {
					PlainTooltip {
						Text(text = stringResource(R.string.action_export))
					}
				},
				state = rememberTooltipState()
			) {
				FloatingActionButton(
					onClick = onExport,
					elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
					shape = MaterialTheme.shapes.extraLarge,
					containerColor = MaterialTheme.colorScheme.secondaryContainer,
					contentColor = MaterialTheme.colorScheme.onSecondaryContainer
				) {
					Icon(
						painter = painterResource(R.drawable.ic_export),
						contentDescription = "Export",
					)
				}
			}
		}
	}
}

private val QRDataType.toActionText: String
	@Composable
	get() = when (this) {
		QRDataType.TYPE_EMAIL -> "Send Mail"
		QRDataType.TYPE_PHONE -> "Call"
		QRDataType.TYPE_GEO -> "Open Map"
		QRDataType.TYPE_URL -> "Open"
		QRDataType.TYPE_WIFI -> "Connect"
		QRDataType.TYPE_TEXT -> "Copy"
		QRDataType.TYPE_SMS -> "Send"
	}