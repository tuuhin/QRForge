package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ShowGeneratedQRButton(
	showButton: Boolean,
	onGenerateQR: () -> Unit,
	modifier: Modifier = Modifier
) {
	AnimatedVisibility(
		visible = showButton,
		enter = slideInVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn),
			initialOffsetY = { height -> height / 4 }
		) + expandVertically(
			expandFrom = Alignment.Bottom,
			animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
		),
		exit = slideOutVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn),
			targetOffsetY = { height -> height / 4 }
		) + shrinkVertically(
			shrinkTowards = Alignment.Bottom,
			animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
		),
		modifier = modifier,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(80.dp)
				.windowInsetsPadding(WindowInsets.navigationBars)
				.sharedBoundsWrapper(SharedTransitionKeys.CREATE_QR_SCREEN_TO_GENERATE_SCREEN),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center
		) {
			Button(
				onClick = onGenerateQR,
				enabled = showButton,
				contentPadding = PaddingValues(vertical = 16.dp),
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
				modifier = Modifier
					.widthIn(min = dimensionResource(R.dimen.bottom_bar_button_min_size))
			) {
				Icon(
					painter = painterResource(R.drawable.ic_qr_simplified),
					contentDescription = "QR code",
					modifier = Modifier.size(28.dp)
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					text = "Preview",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}