package com.sam.qrforge.presentation.common.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.roundToIntSize
import androidx.compose.ui.util.fastMap
import com.sam.qrforge.R
import com.sam.qrforge.data.processing.FloydSteinBergAlgo
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlin.math.roundToInt

@Composable
fun QRTemplateHalfTone(
	model: GeneratedQRUIModel,
	captureLayer: CanvasCaptureLayer,
	modifier: Modifier = Modifier,
	contentMargin: () -> Dp = { 0.dp },
	roundness: () -> Float = { 0f },
	imageScale: () -> Float = { .7f },
	showQRBackground: Boolean = false,
	showTimingPatterns: Boolean = false,
	showAlignmentPatterns: Boolean = false,
	painter: Painter? = null,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderPatternColor: Color = MaterialTheme.colorScheme.primary,
	alignmentPatternColor: Color = MaterialTheme.colorScheme.primary,
	timingPatternColor: Color = MaterialTheme.colorScheme.primary,
	imageColor: Color = MaterialTheme.colorScheme.onBackground,
) {

	val layer = captureLayer.layer ?: rememberGraphicsLayer()

	val finderOffsets = remember(model) { model.finderOffsets() }

	val dataOffsets = remember(model, showAlignmentPatterns) {
		val dataBits = model.dataBitsOffset()
		if (showAlignmentPatterns) {
			val alignmentBits = model.alignmentPatternOffsets()
			dataBits.filter { it in alignmentBits }
		} else dataBits
	}

	val alignmentOffsets = remember(model, showAlignmentPatterns) {
		if (showAlignmentPatterns) model.alignmentPatternOffsets()
		else emptyList()
	}

	val timingOffsets = remember(model, showTimingPatterns) {
		if (showTimingPatterns) model.timingPatternOffsets()
		else emptyList()
	}

	val coercedRoundness = { roundness().coerceIn(0f..1f) }
	val coercedScale = { imageScale().coerceIn(.3f..1f) }
	val coercedMarginWidth = { contentMargin().coerceIn(0.dp, 20.dp) }

	Spacer(
		modifier = modifier
			.defaultMinSize(
				minWidth = dimensionResource(R.dimen.min_qr_size),
				minHeight = dimensionResource(R.dimen.min_qr_size)
			)
			.drawWithCache {

				val limitedRoundness = coercedRoundness()
				val limitedScale = coercedScale()
				val limitMarginWidth = coercedMarginWidth().toPx()
				val scaleFactor = 1 - (2 * limitMarginWidth / size.width)

				val blockSize = size.width / model.widthInBlocks
				val imageBlockSize = (blockSize * .7f).roundToInt()

				val findersBlocks = finderOffsets.fastMap { off -> off.multiply(blockSize) }
				val timingBlocks = timingOffsets.fastMap { off -> off.multiply(blockSize) }
				val dataBits = dataOffsets.fastMap { off -> off.multiply(blockSize) }
				val alignmentBlocks = alignmentOffsets.fastMap { off -> off.multiply(blockSize) }

				val imageSize = size.times(limitedScale)
				val imageOffset = Offset(
					(size.width - imageSize.width) * .5f,
					(size.height - imageSize.height) * .5f
				)
				val image = painterToBitmap(painter)

				val diffusedImage = image?.let {
					FloydSteinBergAlgo.runAlgo(
						bitmap = it,
						finalImageSize = imageSize.roundToIntSize(),
						blockSize = imageBlockSize,
						blackMaskColor = imageColor.toArgb(),
						whiteMaskColor = backgroundColor.toArgb()
					)
				}?.asImageBitmap()

				//smaller blocks
				val moveOffset = (blockSize - imageBlockSize) * .5f
				val smallerBlocks = dataBits.map { it + Offset(moveOffset, moveOffset) }

				onDrawBehind {
					// draw background
					layer.record {
						drawRect(color = backgroundColor)

						// draw full sized qr background
						if (showQRBackground) {
							drawDataBlocks(
								blocks = dataBits,
								blockSize = blockSize,
								scaleFactor = scaleFactor,
								bitsColor = bitsColor,
							)
						}
						// draw the image
						diffusedImage?.let {
							drawImage(
								image = diffusedImage,
								dstOffset = imageOffset.round(),
								dstSize = imageSize.roundToIntSize(),
							)
						}

						// draw the actual qr
						drawDataBlocks(
							blocks = smallerBlocks,
							blockSize = imageBlockSize.toFloat(),
							scaleFactor = scaleFactor,
							roundness = limitedRoundness,
							bitsColor = bitsColor,
						)

						// draw timings
						drawTimingBlocks(
							blocks = timingBlocks,
							blockSize = blockSize,
							evenBitsColor = timingPatternColor,
							oddBitsColor = backgroundColor,
							roundness = limitedRoundness,
							scaleFactor = scaleFactor
						)
						// show alignment patterns
						drawAlignmentBlocks(
							alignments = alignmentBlocks,
							blockSize = blockSize,
							color = alignmentPatternColor,
							scaleFactor = scaleFactor,
							roundness = limitedRoundness
						)
						// draw the finders
						drawFindersClassic(
							finders = findersBlocks,
							blockSize = blockSize,
							color = finderPatternColor,
							backgroundColor = backgroundColor,
							isTransparent = false,
							roundness = limitedRoundness,
							scaleFactor = scaleFactor,
						)
					}
					// draw layer
					drawLayer(layer)
				}
			},
	)
}

private fun CacheDrawScope.painterToBitmap(painter: Painter?): ImageBitmap? {
	if (painter == null) return null
	val imageBitmap = ImageBitmap(
		painter.intrinsicSize.width.roundToInt(),
		painter.intrinsicSize.height.roundToInt()
	)
	val canvas = Canvas(imageBitmap)

	// Draw the painter onto the Canvas backed by the ImageBitmap
	CanvasDrawScope().draw(this, layoutDirection, canvas, painter.intrinsicSize) {
		with(painter) {
			draw(size)
		}
	}
	return imageBitmap
}

@Preview
@Composable
private fun QRTemplateHalfTonePreview() = QRForgeTheme {
	QRTemplateHalfTone(
		model = PreviewFakes.FAKE_GENERATED_UI_MODEL_4,
		captureLayer = CanvasCaptureLayer(),
		painter = painterResource(R.drawable.android_bot),
		modifier = Modifier.size(200.dp)
	)
}