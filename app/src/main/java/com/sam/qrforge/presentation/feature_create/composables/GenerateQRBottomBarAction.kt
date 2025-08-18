package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GenerateQRBottomBarAction(
	showBottomBar: Boolean,
	onGenerateQR: () -> Unit,
	modifier: Modifier = Modifier
) {
	AnimatedVisibility(
		visible = showBottomBar,
		enter = slideInVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + expandVertically(expandFrom = Alignment.Bottom),
		exit = slideOutVertically(
			animationSpec = tween(durationMillis = 200, easing = EaseIn)
		) + shrinkVertically(shrinkTowards = Alignment.Bottom),
		modifier = modifier,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(80.dp)
				.windowInsetsPadding(WindowInsets.navigationBars),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center
		) {
			Button(
				onClick = onGenerateQR,
				enabled = showBottomBar,
				contentPadding = PaddingValues(vertical = 16.dp),
				modifier = Modifier
					.fillMaxWidth(.9f)
					.sharedBoundsWrapper(SharedTransitionKeys.CREATE_QR_SCREEN_TO_GENERATE_SCREEN),
			) {
				Icon(
					painter = painterResource(R.drawable.ic_qr_simplified),
					contentDescription = "QR code",
					modifier = Modifier.size(28.dp)
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(
					text = "Generate",
					style = MaterialTheme.typography.titleMedium
				)
			}
		}
	}
}