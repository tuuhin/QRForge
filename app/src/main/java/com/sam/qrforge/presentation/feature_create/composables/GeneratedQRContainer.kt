package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.coroutines.launch

@Composable
fun GeneratedQRContainerWithActions(
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	isQRGenerationError: Boolean = false,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	val graphicsLayer = rememberGraphicsLayer()
	val scope = rememberCoroutineScope()

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		AnimatedContent(
			targetState = generated != null,
			transitionSpec = { fadeIn() + expandIn() togetherWith fadeOut() + shrinkOut() },
			modifier = Modifier.size(300.dp)
		) { isReady ->
			if (isReady && generated != null)
				QRTemplateBasic(
					model = generated,
					graphicsLayer = graphicsLayer,
					roundness = .5f,
					backgroundColor = Color.Transparent,
					bitsColor = contentColor,
					finderColor = contentColor,
				)
			else GeneratedQRNoContentOrError(isError = isQRGenerationError)
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Button(
				onClick = {
					scope.launch {
						val bitmap = graphicsLayer.toImageBitmap()
					}
				},
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
				shape = MaterialTheme.shapes.medium,
			) {
				Icon(
					painter = painterResource(R.drawable.ic_export),
					contentDescription = "Export"
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(text = "Export")
			}
			Button(
				onClick = {},
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
				shape = MaterialTheme.shapes.medium,
			) {
				Icon(
					painter = painterResource(R.drawable.ic_share),
					contentDescription = "Share"
				)
				Spacer(modifier = Modifier.width(4.dp))
				Text(text = "Share")
			}
		}
	}
}

@Composable
private fun GeneratedQRNoContentOrError(
	modifier: Modifier = Modifier,
	isError: Boolean = false,
	borderColor: Color = MaterialTheme.colorScheme.tertiary,
	textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	Box(
		modifier = modifier
			.size(120.dp, 80.dp)
			.drawWithContent {
				drawRoundRect(
					color = borderColor,
					cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
					style = Stroke(
						width = 2.dp.toPx(),
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
				modifier = Modifier.size(72.dp)
			)
			Text(
				text = if (!isError) "No content yet" else "Cannot render QR",
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				color = textColor,

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
private fun GeneratedQRContainerPreview(
	@PreviewParameter(GeneratedQRNullablePreviewParams::class)
	generated: GeneratedQRUIModel?
) = QRForgeTheme {
	Surface {
		GeneratedQRContainerWithActions(
			generated = generated,
			modifier = Modifier.width(IntrinsicSize.Max)
		)
	}
}