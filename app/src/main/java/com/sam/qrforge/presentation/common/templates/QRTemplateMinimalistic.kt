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
private fun QRTemplateMinimalistic(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	roundness: Float = 0f,
	bitsSizeMultiplier: Float = .5f,
	contentMargin: Dp = 0.dp,
	isDiamond: Boolean = false,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	backgroundColor: Color? = null,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderColor: Color = MaterialTheme.colorScheme.onBackground,
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
					val limitRoundness = roundness.coerceIn(0f..1f)
					val bitsMultiplier = bitsSizeMultiplier.coerceIn(.2f..1.5f)
					val limitMarginWidth = contentMargin.coerceIn(0.dp, 20.dp).toPx()
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
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
) {
	QRTemplateMinimalistic(
		model = model,
		roundness = decoration.roundness,
		bitsSizeMultiplier = decoration.bitsSizeMultiplier,
		isDiamond = decoration.isDiamond,
		contentMargin = decoration.contentMargin,
		bitsColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
		finderColor = decoration.findersColor ?: MaterialTheme.colorScheme.onBackground,
		backgroundColor = if (decoration.showBackground) MaterialTheme.colorScheme.background else null,
		graphicsLayer = graphicsLayer,
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