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
private fun QRTemplateMinimalistic(
	model: GeneratedQRUIModel,
	graphicsLayer: CanvasCaptureLayer,
	modifier: Modifier = Modifier,
	roundness: () -> Float = { 0f },
	bitsSizeMultiplier: () -> Float = { .5f },
	contentMargin: () -> Dp = { 0.dp },
	isDiamond: Boolean = false,
	backgroundColor: Color? = null,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderColor: Color = MaterialTheme.colorScheme.onBackground,
) {

	val layer = graphicsLayer.layer ?: rememberGraphicsLayer()

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
					val limitRoundness = coercedRoundness()
					val bitsMultiplier = coercedBitsMultiplier()
					val limitMarginWidth = coercedMarginWidth().toPx()

					val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

					// draw background
					layer.record {
						if (backgroundColor != null) drawRect(color = backgroundColor)

						// blocks
						drawDataBlocks(
							blocks = blocks,
							blockSize = blockSize,
							scaleFactor = scaleFactor,
							bitsColor = bitsColor,
							roundness = limitRoundness,
							multiplier = bitsMultiplier,
							isDiamond = isDiamond,
						)

						// draw the finders
						drawFindersMinimalistic(
							finders = finders,
							blockSize = blockSize,
							color = finderColor,
							isDiamond = isDiamond,
							roundness = limitRoundness,
							scaleFactor = scaleFactor
						)
					}
					drawLayer(layer)
				}
			},
	)
}

@Composable
fun QRTemplateMinimalistic(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption.QRDecorationOptionMinimal = QRDecorationOption.QRDecorationOptionMinimal(),
	captureLayer: CanvasCaptureLayer = CanvasCaptureLayer(),
) {
	val currentRoundness by rememberUpdatedState(decoration.roundness)
	val currentBitsSizeMultiplier by rememberUpdatedState(decoration.bitsSizeMultiplier)
	val currentContentMargin by rememberUpdatedState(decoration.contentMargin)

	QRTemplateMinimalistic(
		model = model,
		roundness = { currentRoundness },
		bitsSizeMultiplier = { currentBitsSizeMultiplier },
		contentMargin = { currentContentMargin },
		isDiamond = decoration.isDiamond,
		bitsColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
		finderColor = decoration.findersColor ?: MaterialTheme.colorScheme.onBackground,
		backgroundColor = decoration.backGroundColor,
		graphicsLayer = captureLayer,
		modifier = modifier,
	)
}

@PreviewLightDark
@Composable
private fun QRTemplateMinimalisticPreview() = QRForgeTheme {
	Surface {
		QRTemplateMinimalistic(
			model = PreviewFakes.FAKE_GENERATED_UI_MODEL,
			modifier = Modifier.size(200.dp)
		)
	}
}