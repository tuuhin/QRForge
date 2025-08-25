package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRColorLayer
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.templates.QRTemplateLayered
import com.sam.qrforge.presentation.common.templates.QRTemplateMinimalistic

@Composable
fun QRMasterTemplate(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	size: DpSize = DpSize(300.dp, 300.dp),
	graphicsLayer: GraphicsLayer = rememberGraphicsLayer(),
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
) {
	AnimatedContent(
		targetState = decoration.templateType,
		modifier = modifier,
		transitionSpec = {
			fadeIn(initialAlpha = .3f) + scaleIn(
				animationSpec = tween(400, easing = EaseInExpo)
			) togetherWith fadeOut(targetAlpha = .2f) + scaleOut(
				animationSpec = tween(durationMillis = 400, easing = EaseOut)
			)
		},
		contentAlignment = Alignment.Center
	) { template ->
		when (template) {
			QRTemplateOption.BASIC -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionBasic
				QRTemplateBasic(
					model = model,
					roundness = type?.roundness ?: 0f,
					bitsSizeMultiplier = type?.bitsSizeMultiplier ?: 1f,
					isDiamond = type?.isDiamond ?: false,
					contentMargin = type?.contentMargin ?: 0.dp,
					finderColor = type?.findersColor ?: MaterialTheme.colorScheme.onBackground,
					bitsColor = type?.bitsColor ?: MaterialTheme.colorScheme.onBackground,
					backgroundColor = type?.backGroundColor ?: Color.Transparent,
					showFrame = type?.showFrame ?: false,
					frameColor = type?.frameColor ?: MaterialTheme.colorScheme.onBackground,
					graphicsLayer = graphicsLayer,
					modifier = Modifier.size(size)
				)
			}

			QRTemplateOption.MINIMALISTIC -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionMinimal

				QRTemplateMinimalistic(
					model = model,
					roundness = type?.roundness ?: 0f,
					bitsSizeMultiplier = type?.bitsSizeMultiplier ?: 1f,
					isDiamond = type?.isDiamond ?: false,
					contentMargin = type?.contentMargin ?: 0.dp,
					bitsColor = type?.bitsColor ?: MaterialTheme.colorScheme.onBackground,
					finderColor = type?.findersColor ?: MaterialTheme.colorScheme.onBackground,
					showBackground = type?.showBackground ?: false,
					modifier = Modifier.size(size)
				)

			}

			QRTemplateOption.COLOR_LAYERED -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionColorLayer
				QRTemplateLayered(
					model = model,
					coloredLayers = type?.coloredLayers ?: { QRColorLayer.COLOR_BLOCKS },
					roundness = type?.roundness ?: 0f,
					bitsSizeMultiplier = type?.bitsSizeMultiplier ?: 1f,
					isDiamond = type?.isDiamond ?: false,
					contentMargin = type?.contentMargin ?: 0.dp,
					backgroundColor = type?.backGroundColor ?: MaterialTheme.colorScheme.background,
					modifier = Modifier.size(size)
				)
			}
		}
	}
}