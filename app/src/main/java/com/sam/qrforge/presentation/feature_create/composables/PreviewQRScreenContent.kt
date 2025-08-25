package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.presentation.common.composables.FailedOrMissingQRContent
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic

@Composable
fun PreviewQRScreenContent(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {

	val isQRPresent by remember(generated) {
		derivedStateOf { generated != null }
	}

	Column(
		modifier = modifier.padding(contentPadding),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		AnimatedContent(
			targetState = isQRPresent,
			transitionSpec = {
				fadeIn(
					initialAlpha = .4f,
					animationSpec = tween(durationMillis = 200, easing = EaseInBounce)
				) + expandIn() togetherWith fadeOut(
					targetAlpha = .1f,
					animationSpec = tween(durationMillis = 90)
				) + shrinkOut()
			},
			modifier = Modifier.size(300.dp)
		) { isReady ->
			if (isReady && generated != null)
				QRTemplateBasic(
					model = generated,
					modifier = Modifier.fillMaxSize()
				)
			else FailedOrMissingQRContent(isError = false)
		}
		Spacer(modifier = Modifier.heightIn(20.dp))
		ContentStringCard(content = content)
	}
}