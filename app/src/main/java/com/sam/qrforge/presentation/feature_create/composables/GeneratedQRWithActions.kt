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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
) {
	val graphicsLayer = rememberGraphicsLayer()

	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		AnimatedContent(
			targetState = generated != null,
			transitionSpec = { fadeIn() + expandIn() togetherWith fadeOut() + shrinkOut() },
			modifier = Modifier.size(300.dp)
		) { isReady ->
			if (isReady && generated != null)
				QRMasterTemplate(
					model = generated,
					decoration = templateDecoration,
					graphicsLayer = graphicsLayer
				)
			else GeneratedQRNoContentOrError(isError = isQRGenerationError)
		}
		ExtendedFloatingActionButton(
			onClick = {},
			elevation = FloatingActionButtonDefaults.loweredElevation(defaultElevation = 0.dp),
			shape = MaterialTheme.shapes.large,
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
private fun GeneratedQWithActionsPreview(
	@PreviewParameter(GeneratedQRNullablePreviewParams::class)
	generated: GeneratedQRUIModel?
) = QRForgeTheme {
	Surface {
		GeneratedQRWithActions(
			generated = generated,
			modifier = Modifier.width(IntrinsicSize.Max)
		)
	}
}