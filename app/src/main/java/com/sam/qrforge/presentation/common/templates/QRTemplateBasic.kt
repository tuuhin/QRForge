package com.sam.qrforge.presentation.common.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
private fun QRTemplateBasic(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	roundness: () -> Float = { 0f },
	bitsSizeMultiplier: () -> Float = { 1f },
	contentMargin: () -> Dp = { 0.dp },
	showFrame: Boolean = false,
	isDiamond: Boolean = false,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderColor: Color = MaterialTheme.colorScheme.onBackground,
	frameColor: Color = MaterialTheme.colorScheme.primary,
) {
	val layer = graphicsLayer?.invoke() ?: rememberGraphicsLayer()

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
					val limitRoundness = roundness().coerceIn(0f..1f)
					val bitsMultiplier = bitsSizeMultiplier().coerceIn(.2f..1.5f)
					val limitMarginWidth = contentMargin().coerceIn(0.dp, 20.dp).toPx()


					layer.record {    // draw background
						drawRect(color = backgroundColor)

						val totalMargin = limitMarginWidth + (model.margin * blockSize)
						val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

						if (showFrame) drawFrame(totalMargin, blockSize, frameColor, limitRoundness)

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
						drawFindersClassic(
							finders = finders,
							blockSize = blockSize,
							color = finderColor,
							isDiamond = isDiamond,
							roundness = limitRoundness,
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
	decoration: QRDecorationOption.QRDecorationOptionBasic = QRDecorationOption.QRDecorationOptionBasic(),
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
) {
	QRTemplateBasic(
		model = model,
		roundness = { decoration.roundness },
		bitsSizeMultiplier = { decoration.bitsSizeMultiplier },
		contentMargin = { decoration.contentMargin },
		isDiamond = decoration.isDiamond,
		showFrame = decoration.showFrame,
		finderColor = decoration.findersColor ?: MaterialTheme.colorScheme.onBackground,
		bitsColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
		backgroundColor = decoration.backGroundColor ?: Color.Transparent,
		frameColor = decoration.frameColor ?: MaterialTheme.colorScheme.onBackground,
		graphicsLayer = graphicsLayer,
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