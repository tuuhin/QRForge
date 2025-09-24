package com.sam.qrforge.presentation.common.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.LayeredQRColors
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
private fun QRTemplateLayered(
	model: GeneratedQRUIModel,
	captureLayer: CanvasCaptureLayer,
	modifier: Modifier = Modifier,
	contentMargin: () -> Dp = { 0.dp },
	roundness: () -> Float = { 0f },
	bitsSizeMultiplier: () -> Float = { 1f },
	coloredLayers: LayeredQRColors = LayeredQRColors.Blocks,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	fallbackContentColor: Color = MaterialTheme.colorScheme.onBackground,
	fallbackBlendMode: BlendMode = BlendMode.SrcIn,
) {

	val layer = captureLayer.layer ?: rememberGraphicsLayer()

	val filteredColorLayers = remember(coloredLayers, fallbackContentColor) {
		coloredLayers.copyEnsureOneExists(fallbackContentColor)
			.filterValidOverlayColor
	}

	val finderOffsets = remember(model) { model.finderOffsets() }
	val dataOffsets = remember(model) { model.dataBitsOffset() }

	val coercedRoundness = { roundness().coerceIn(0f..1f) }
	val coercedBitsMultiplier = { bitsSizeMultiplier().coerceIn(.2f..1.5f) }
	val coercedMarginWidth = { contentMargin().coerceIn(0.dp, 20.dp) }

	Spacer(
		modifier = modifier
			.defaultMinSize(
				minWidth = dimensionResource(R.dimen.min_qr_size),
				minHeight = dimensionResource(R.dimen.min_qr_size)
			)
			.drawWithCache {
				val blockSize = size.width / model.widthInBlocks

				val finders = finderOffsets.fastMap { off -> off.multiply(blockSize) }
				val blocks = dataOffsets.fastMap { offset -> offset.multiply(blockSize) }

				onDrawBehind {


					val limitedRoundness = coercedRoundness()
					val bitsMultiplier = coercedBitsMultiplier()
					val limitMarginWidth = coercedMarginWidth().toPx()
					val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

					layer.record {
						// draw background
						drawRect(color = backgroundColor)
						// draw the blocks

						drawLayeredDataBlocks(
							baseOffset = blocks,
							layers = filteredColorLayers,
							blockSize = blockSize,
							scaleFactor = scaleFactor,
							roundness = limitedRoundness,
							multiplier = bitsMultiplier,
							fallbackBlendMode = fallbackBlendMode
						)

						// draw blocks
						for (layer in filteredColorLayers) {
							drawFindersClassic(
								finders = finders,
								blockSize = blockSize,
								fractionOffset = layer.offset,
								roundness = limitedRoundness,
								scaleFactor = scaleFactor,
								color = layer.color,
								blendMode = layer.blendMode ?: fallbackBlendMode
							)
						}
					}
					// draw the layer
					drawLayer(layer)
				}
			},
	)
}

@Composable
fun QRTemplateLayered(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	captureLayer: CanvasCaptureLayer = CanvasCaptureLayer(),
	decoration: QRDecorationOption.QRDecorationOptionColorLayer = QRDecorationOption.QRDecorationOptionColorLayer()
) {
	val currentRoundness by rememberUpdatedState(decoration.roundness)
	val currentBitsSizeMultiplier by rememberUpdatedState(decoration.bitsSizeMultiplier)
	val currentContentMargin by rememberUpdatedState(decoration.contentMargin)

	QRTemplateLayered(
		model = model,
		coloredLayers = decoration.coloredLayers,
		roundness = { currentRoundness },
		bitsSizeMultiplier = { currentBitsSizeMultiplier },
		contentMargin = { currentContentMargin },
		backgroundColor = decoration.backGroundColor ?: Color.Transparent,
		captureLayer = captureLayer,
		modifier = modifier,
	)
}

@PreviewLightDark
@Composable
private fun QRTemplateLayeredPreview() = QRForgeTheme {
	QRTemplateLayered(
		model = PreviewFakes.FAKE_GENERATED_UI_MODEL,
		decoration = QRDecorationOption.QRDecorationOptionColorLayer(
			backGroundColor = MaterialTheme.colorScheme.background,
			coloredLayers = LayeredQRColors.PowerRangers,
		),
		modifier = Modifier.size(300.dp),
	)
}