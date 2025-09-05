package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.templates.QRTemplateLayered
import com.sam.qrforge.presentation.common.templates.QRTemplateMinimalistic

@Composable
fun QRMasterTemplate(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
) {

	AnimatedContent(
		targetState = decoration.templateType,
		transitionSpec = {
			fadeIn(initialAlpha = .3f) + scaleIn(
				animationSpec = tween(400, easing = EaseInCubic)
			) togetherWith fadeOut(targetAlpha = .2f) + scaleOut(
				animationSpec = tween(durationMillis = 400, easing = EaseOut)
			)
		},
		contentAlignment = Alignment.Center,
		modifier = modifier.defaultMinSize(minWidth = 120.dp, 120.dp),
	) { template ->
		when (template) {
			QRTemplateOption.BASIC -> {
				val decor = decoration as? QRDecorationOption.QRDecorationOptionBasic
				QRTemplateBasic(
					model = model,
					decoration = decor ?: QRDecorationOption.QRDecorationOptionBasic(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.fillMaxSize()
				)
			}

			QRTemplateOption.MINIMALISTIC -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionMinimal

				QRTemplateMinimalistic(
					model = model,
					decoration = type ?: QRDecorationOption.QRDecorationOptionMinimal(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.fillMaxSize(),
				)
			}

			QRTemplateOption.COLOR_LAYERED -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionColorLayer
				QRTemplateLayered(
					model = model,
					decoration = type ?: QRDecorationOption.QRDecorationOptionColorLayer(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}