package com.sam.qrforge.presentation.common.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRColorLayer
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRTemplateLayered(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	coloredLayers: () -> QRColorLayer = { QRColorLayer() },
	contentMargin: Dp = 0.dp,
	roundness: Float = 0f,
	bitsSizeMultiplier: Float = 1f,
	isDiamond: Boolean = false,
	graphicsLayer: GraphicsLayer = rememberGraphicsLayer(),
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	fallbackContentColor: Color = MaterialTheme.colorScheme.onBackground,
	fallbackBlendMode: BlendMode = BlendMode.Difference,
) {
	Spacer(
		modifier = modifier
			.defaultMinSize(
				minWidth = dimensionResource(R.dimen.min_qr_size),
				minHeight = dimensionResource(R.dimen.min_qr_size)
			)
			.drawWithCache {

				val blockSize = size.width / model.widthInBlocks

				val finders = model.finderOffsets(blockSize)
				val blocks = model.dataBitsOffset(blockSize)

				onDrawBehind {

					val layers = coloredLayers().copyEnsureOneExists(fallbackContentColor)
						.copyEnsuresOnlyValidOffsets()
						.layers

					val limitMarginWidth = contentMargin.coerceIn(0.dp, 20.dp).toPx()
					val limitedRoundness = roundness.coerceIn(0f..1f)
					val bitsMultiplier = bitsSizeMultiplier.coerceIn(.2f..1.5f)

					graphicsLayer.record {// draw background
						drawRect(color = backgroundColor)

						val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

						// draw blocks
						for (layer in layers) {
							drawDataBlocks(
								blocks = blocks,
								blockSize,
								fractionOffset = layer.offset,
								bitsColor = layer.color,
								scaleFactor = scaleFactor,
								roundness = limitedRoundness,
								multiplier = bitsMultiplier,
								isDiamond = isDiamond,
								blendMode = layer.blendMode ?: fallbackBlendMode
							)
							drawFindersClassic(
								finders = finders,
								blockSize = blockSize,
								fractionOffset = layer.offset,
								roundness = limitedRoundness,
								scaleFactor = scaleFactor,
								isDiamond = isDiamond,
								color = layer.color,
								blendMode = layer.blendMode ?: fallbackBlendMode
							)
						}
					}
					// draw the layer
					drawLayer(graphicsLayer)
				}
			},
	)
}

@PreviewLightDark
@Composable
private fun QRTemplateLayeredPreview() = QRForgeTheme {
	QRTemplateLayered(
		model = PreviewFakes.FAKE_GENERATED_UI_MODEL,
		backgroundColor = MaterialTheme.colorScheme.background,
		coloredLayers = { QRColorLayer.COLOR_BLOCKS }
	)
}