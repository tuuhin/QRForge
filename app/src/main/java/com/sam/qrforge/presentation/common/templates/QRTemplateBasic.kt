package com.sam.qrforge.presentation.common.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
private fun QRTemplateBasic(
	model: GeneratedQRUIModel,
	captureLayer: CanvasCaptureLayer,
	modifier: Modifier = Modifier,
	roundness: () -> Float = { 0f },
	bitsSizeMultiplier: () -> Float = { 1f },
	contentMargin: () -> Dp = { 0.dp },
	showFrame: Boolean = false,
	isDiamond: Boolean = false,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderColor: Color = MaterialTheme.colorScheme.onBackground,
	frameColor: Color = MaterialTheme.colorScheme.primary,
) {

	val layer = captureLayer.layer ?: rememberGraphicsLayer()

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
				val blocks = dataOffsets.fastMap { off -> off.multiply(blockSize) }

				onDrawBehind {
					val limitedRoundness = coercedRoundness()
					val bitsMultiplier = coercedBitsMultiplier()
					val limitMarginWidth = coercedMarginWidth().toPx()

					val totalMargin = limitMarginWidth + (model.margin * blockSize)
					val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

					layer.record {
						// draw background
						drawRect(color = backgroundColor)

						if (showFrame) drawFrame(
							totalMargin = totalMargin,
							blockSize = blockSize,
							frameColor = frameColor,
							roundness = limitedRoundness
						)

						// blocks
						drawDataBlocks(
							blocks = blocks,
							blockSize = blockSize,
							scaleFactor = scaleFactor,
							bitsColor = bitsColor,
							roundness = limitedRoundness,
							multiplier = bitsMultiplier,
							isDiamond = isDiamond,
						)

						// draw the finders
						drawFindersClassic(
							finders = finders,
							blockSize = blockSize,
							color = finderColor,
							isDiamond = isDiamond,
							roundness = limitedRoundness,
							scaleFactor = scaleFactor
						)
					}
					//draw the layer
					drawLayer(layer)
				}
			},
	)
}

@Composable
fun QRTemplateBasic(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	captureLayer: CanvasCaptureLayer = CanvasCaptureLayer(),
	decoration: QRDecorationOption.QRDecorationOptionBasic = QRDecorationOption.QRDecorationOptionBasic(),
) {
	val currentRoundness by rememberUpdatedState(decoration.roundness)
	val currentBitsSizeMultiplier by rememberUpdatedState(decoration.bitsSizeMultiplier)
	val currentContentMargin by rememberUpdatedState(decoration.contentMargin)

	QRTemplateBasic(
		model = model,
		roundness = { currentRoundness },
		bitsSizeMultiplier = { currentBitsSizeMultiplier },
		contentMargin = { currentContentMargin },
		isDiamond = decoration.isDiamond,
		showFrame = decoration.showFrame,
		finderColor = decoration.findersColor ?: MaterialTheme.colorScheme.onBackground,
		bitsColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
		backgroundColor = decoration.backGroundColor
			?: MaterialTheme.colorScheme.surfaceContainerLow,
		frameColor = decoration.frameColor ?: MaterialTheme.colorScheme.onBackground,
		captureLayer = captureLayer,
		modifier = modifier,
	)
}


@PreviewLightDark
@Composable
private fun QRTemplateBasicPreview() = QRForgeTheme {
	Surface {
		QRTemplateBasic(
			model = PreviewFakes.FAKE_GENERATED_UI_MODEL_4,
			modifier = Modifier.size(200.dp)
		)
	}
}