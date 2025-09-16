package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@Composable
fun ExportShareActions(
	onShare: () -> Unit,
	onExport: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	AnimatedVisibility(
		visible = enabled,
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
			horizontalArrangement = Arrangement.Center
		) {
			FloatingActionButton(
				onClick = onShare,
				elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
				shape = MaterialTheme.shapes.extraLarge,
				containerColor = MaterialTheme.colorScheme.secondaryContainer,
				contentColor = MaterialTheme.colorScheme.onSecondaryContainer
			) {
				Icon(
					painter = painterResource(R.drawable.ic_share),
					contentDescription = "Share icon"
				)
			}
			Spacer(modifier = Modifier.width(20.dp))
			ExtendedFloatingActionButton(
				onClick = onExport,
				elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
				shape = MaterialTheme.shapes.extraLarge,
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
			) {
				Icon(
					painter = painterResource(R.drawable.ic_export),
					contentDescription = "Export",
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = stringResource(R.string.action_export),
					style = MaterialTheme.typography.titleMedium
				)
			}
		}
	}
}