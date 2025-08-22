package com.sam.qrforge.presentation.feature_create.composables

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.QRMasterTemplate
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun GeneratedQRWithActions(
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	isQRGenerationError: Boolean = false,
	templateDecoration: QRDecorationOption = QRDecorationOptionBasic(),
	backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	backgroundShape: Shape = MaterialTheme.shapes.extraLarge,
) {
	val graphicsLayer = rememberGraphicsLayer()

	val isQRPresent by remember(generated) {
		derivedStateOf { generated != null }
	}

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Surface(
			shape = backgroundShape,
			color = backgroundColor,
			modifier = Modifier.size(300.dp)
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
				modifier = Modifier.fillMaxSize()
			) { isReady ->
				if (isReady && generated != null)
					QRMasterTemplate(
						model = generated,
						decoration = templateDecoration,
						graphicsLayer = graphicsLayer
					)
				else MissingQRContent(isError = isQRGenerationError)
			}
		}
		CommonQRActions(onExport = {}, onShare = {})
	}
}

@Composable
private fun CommonQRActions(
	onExport: () -> Unit,
	onShare: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
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
		ExtendedFloatingActionButton(
			onClick = onExport,
			elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
			shape = MaterialTheme.shapes.extraLarge,
			containerColor = MaterialTheme.colorScheme.primaryContainer,
			contentColor = MaterialTheme.colorScheme.onPrimaryContainer
		) {
			Icon(
				painter = painterResource(R.drawable.ic_export),
				contentDescription = "Export",
			)
			Spacer(modifier = Modifier.width(4.dp))
			Text(text = "Export", style = MaterialTheme.typography.titleMedium)
		}
	}
}

@Composable
private fun MissingQRContent(
	modifier: Modifier = Modifier,
	isError: Boolean = false,
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
				text = if (!isError) "No content yet" else "Cannot render QR",
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				color = contentColor,
			)
		}
	}
}

private class GeneratedQRNullablePreviewParams :
	CollectionPreviewParameterProvider<GeneratedQRUIModel?>(
		listOf(
			PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
			PreviewFakes.FAKE_GENERATED_UI_MODEL,
			null
		)
	)

@Preview
@Composable
private fun GeneratedQWithActionsPreview(
	@PreviewParameter(GeneratedQRNullablePreviewParams::class)
	generated: GeneratedQRUIModel?
) = QRForgeTheme {
	Surface {
		GeneratedQRWithActions(
			generated = generated,
			modifier = Modifier.padding(32.dp)
		)
	}
}