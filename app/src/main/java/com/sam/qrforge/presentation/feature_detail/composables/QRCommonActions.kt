package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
	isQRReady: Boolean,
	onShare: () -> Unit,
	onExport: () -> Unit,
	modifier: Modifier = Modifier,
	hasAssociatedAction: Boolean = true,
	onAction: () -> Unit = {},
	type: QRDataType = QRDataType.TYPE_TEXT,
) {
	AnimatedVisibility(
		visible = isQRReady,
		enter = slideInVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + expandVertically(
			expandFrom = Alignment.Bottom,
			animationSpec = spring(
				dampingRatio = Spring.DampingRatioLowBouncy,
				stiffness = Spring.StiffnessMedium
			)
		),
		exit = slideOutVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + shrinkVertically(
			shrinkTowards = Alignment.Bottom, animationSpec = spring(
				dampingRatio = Spring.DampingRatioLowBouncy,
				stiffness = Spring.StiffnessMedium
			)
		),
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
			TooltipBox(
				positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
				tooltip = {
					PlainTooltip {
						Text(text = stringResource(R.string.action_export))
					}
				},
				state = rememberTooltipState()
			) {
				SmallFloatingActionButton(
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

private val QRDataType.toActionText: String?
	@Composable
	get() = when (this) {
		QRDataType.TYPE_EMAIL -> stringResource(R.string.qr_action_text_mail)
		QRDataType.TYPE_PHONE -> stringResource(R.string.qr_action_text_phone)
		QRDataType.TYPE_GEO -> stringResource(R.string.qr_action_text_geo)
		QRDataType.TYPE_URL -> stringResource(R.string.qr_action_text_url)
		QRDataType.TYPE_WIFI -> stringResource(R.string.qr_action_text_wifi)
		QRDataType.TYPE_SMS -> stringResource(R.string.qr_action_text_sms)
		else -> null
	}