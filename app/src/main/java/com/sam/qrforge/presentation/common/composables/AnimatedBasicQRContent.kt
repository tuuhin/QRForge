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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic

@Composable
fun AnimatedBasicQRContent(
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	graphicsLayer: @Composable () -> GraphicsLayer = { rememberGraphicsLayer() },
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	shape: Shape = MaterialTheme.shapes.extraLarge,
) {

	val isQRPresent by remember(generated) {
		derivedStateOf { generated != null }
	}

	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier.sizeIn(300.dp),
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
					backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
					graphicsLayer = graphicsLayer,
					roundness = .5f,
					contentMargin = 0.dp,
					modifier = Modifier.fillMaxSize()
				)
			else FailedOrMissingQRContent(errorMessage = "QR Content not found")
		}
	}
}

@Composable
private fun FailedOrMissingQRContent(
	modifier: Modifier = Modifier,
	errorMessage: String,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	stroke: BorderStroke = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
) {
	Box(
		modifier = modifier
			.size(120.dp, 80.dp)
			.drawWithContent {
				drawRoundRect(
					brush = stroke.brush,
					cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
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
			verticalArrangement = Arrangement.spacedBy(6.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				painter = painterResource(R.drawable.ic_content_missing),
				contentDescription = "Missing content",
				tint = contentColor,
				modifier = Modifier.size(72.dp)
			)
			Text(
				text = errorMessage,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				color = contentColor,
			)
		}
	}
}