package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sam.qrforge.R

@Composable
fun QREditActionButton(
	showButton: Boolean,
	onEdit: () -> Unit,
	modifier: Modifier = Modifier,
	isExpanded: Boolean = true,
	shape: Shape = MaterialTheme.shapes.extraLarge,
	containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
	contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
	AnimatedVisibility(
		visible = showButton,
		enter = slideInHorizontally(
			animationSpec = tween(durationMillis = 200, easing = EaseInOut)
		) + fadeIn(),
		exit = slideOutVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseInOut)
		) + fadeOut(),
		modifier = modifier,
	) {
		ExtendedFloatingActionButton(
			onClick = onEdit,
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_edit),
					contentDescription = "Edit"
				)
			},
			shape = shape,
			expanded = isExpanded,
			containerColor = containerColor,
			contentColor = contentColor,
			elevation = FloatingActionButtonDefaults.loweredElevation(),
			text = { Text(text = stringResource(R.string.action_edit)) },
		)
	}
}