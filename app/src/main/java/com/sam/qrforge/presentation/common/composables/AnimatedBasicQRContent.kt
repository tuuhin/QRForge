package com.sam.qrforge.presentation.common.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic

@Composable
fun AnimatedBasicQRContent(
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	graphicsLayer: @Composable () -> GraphicsLayer = { rememberGraphicsLayer() },
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	shape: Shape = MaterialTheme.shapes.extraLarge,
) {

	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier.sizeIn(300.dp),
	) {
		AnimatedContent(
			targetState = generated != null,
			transitionSpec = {
				fadeIn(
					initialAlpha = .4f,
					animationSpec = tween(durationMillis = 200, easing = EaseInBounce)
				) + expandIn(expandFrom = Alignment.Center) togetherWith fadeOut(
					targetAlpha = .1f,
					animationSpec = tween(durationMillis = 90)
				) + shrinkOut(shrinkTowards = Alignment.Center)
			},
			modifier = Modifier.size(300.dp)
		) { isReady ->
			if (isReady && generated != null)
				QRTemplateBasic(
					model = generated,
					graphicsLayer = graphicsLayer,
					decoration = QRDecorationOption.QRDecorationOptionBasic(
						roundness = .5f,
						contentMargin = 0.dp
					),
					modifier = Modifier.fillMaxSize()
				)
			else FailedOrMissingQRContent(
				errorMessage = stringResource(R.string.qr_content_missing),
				modifier = Modifier.padding(24.dp)
			)
		}
	}
}

@Composable
private fun FailedOrMissingQRContent(
	errorMessage: String,
	modifier: Modifier = Modifier,
	contentColor: Color = MaterialTheme.colorScheme.secondary,
	shape: Shape = MaterialTheme.shapes.large,
	stroke: BorderStroke = BorderStroke(3.dp, MaterialTheme.colorScheme.secondary)
) {
	Box(
		modifier = modifier
			.size(120.dp, 80.dp)
			.drawWithContent {
				drawOutline(
					outline = shape.createOutline(size, layoutDirection, this),
					brush = stroke.brush,
					style = Stroke(
						width = stroke.width.toPx(),
						pathEffect = PathEffect.dashPathEffect(
							intervals = floatArrayOf(40f, 40f)
						)
					)
				)
				drawContent()
			},
		contentAlignment = Alignment.Center
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				painter = painterResource(R.drawable.ic_content_missing),
				contentDescription = "Missing content",
				tint = contentColor,
				modifier = Modifier.size(80.dp)
			)
			Text(
				text = errorMessage,
				style = MaterialTheme.typography.titleMedium,
				color = contentColor,
			)
		}
	}
}