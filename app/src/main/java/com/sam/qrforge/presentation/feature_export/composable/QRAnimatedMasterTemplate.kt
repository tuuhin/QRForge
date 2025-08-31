package com.sam.qrforge.presentation.feature_export.composable

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRAnimatedMasterTemplate(
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
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
			}
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
private fun QRAnimatedMasterTemplatePreview(
	@PreviewParameter(GeneratedQRNullablePreviewParams::class)
	generated: GeneratedQRUIModel?
) = QRForgeTheme {
	Surface {
		QRAnimatedMasterTemplate(
			generated = generated,
			modifier = Modifier.padding(32.dp)
		)
	}
}