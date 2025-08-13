package com.sam.qrforge.feature_generator.presentation.templates

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.roundToIntSize
import com.sam.qrforge.R
import com.sam.qrforge.feature_generator.data.processing.FloydSteinBergAlgo
import com.sam.qrforge.feature_generator.presentation.models.QRUIModel
import com.sam.qrforge.feature_generator.presentation.util.GeneratorPreviewFake
import com.sam.qrforge.feature_generator.presentation.util.alignmentPatternOffsets
import com.sam.qrforge.feature_generator.presentation.util.dataBitsOffset
import com.sam.qrforge.feature_generator.presentation.util.finderOffsets
import com.sam.qrforge.feature_generator.presentation.util.timingPatternOffsets
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlin.math.roundToInt

@Composable
fun QRTemplateHalfTone(
	model: QRUIModel,
	contentMargin: Dp = 10.dp,
	modifier: Modifier = Modifier,
	roundness: Float = 1f,
	imageScale: Float = .7f,
	showQRBackground: Boolean = false,
	showTimingPatterns: Boolean = false,
	showAlignmentPatterns: Boolean = true,
	image: ImageBitmap? = null,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
	bitsColor: Color = MaterialTheme.colorScheme.onBackground,
	finderPatternColor: Color = MaterialTheme.colorScheme.primary,
	alignmentPatternColor: Color = MaterialTheme.colorScheme.primary,
	timingPatternColor: Color = MaterialTheme.colorScheme.primary,
	imageColor: Color = MaterialTheme.colorScheme.onBackground,
) {
	Spacer(
		modifier = modifier
			.defaultMinSize(
				minWidth = dimensionResource(R.dimen.min_qr_size),
				minHeight = dimensionResource(R.dimen.min_qr_size)
			)
			.drawWithCache {

				val marginWidth = contentMargin.coerceIn(0.dp, 20.dp).toPx()
				val limitedScale = imageScale.coerceIn(.1f..1f)
				val limitedRoundness = roundness.coerceIn(0f..1f)

				val blockSize = size.width / model.widthInBlocks
				val imageBlockSize = (blockSize * .7f).roundToInt()

				val scaleFactor = 1 - (2 * marginWidth / size.width)

				val findersBlocks = model.finderOffsets(blockSize)
				val timingBlocks = if (showTimingPatterns) model.timingPatternOffsets(blockSize)
				else emptyList()

				val alignmentBlocks = if (showAlignmentPatterns)
					model.alignmentPatternOffsets(blockSize) else emptyList()

				val alignmentsAsSize = alignmentBlocks
					.map { it to Size(blockSize, blockSize).times(4f) }
					.map { it.first.x..<it.first.x + it.second.width to it.first.y..<it.first.y + it.second.height }

				val blocks = model.dataBitsOffset(blockSize).filterNot { offset ->
					alignmentsAsSize.any { offset.x in it.first && offset.y in it.second }
				}

				val imageSize = size.times(limitedScale)
				val imageOffset = Offset(
					(size.width - imageSize.width) * .5f,
					(size.height - imageSize.height) * .5f
				)

				val diffusedImage = image?.let {
					FloydSteinBergAlgo.runAlgo(
						bitmap = image,
						finalImageSize = imageSize.roundToIntSize(),
						blockSize = imageBlockSize,
						blackMaskColor = imageColor.toArgb(),
						whiteMaskColor = backgroundColor.toArgb()
					)
				}?.asImageBitmap()

				//smaller blocks
				val moveOffset = (blockSize - imageBlockSize) * .5f
				val smallerBlocks = blocks.map { it + Offset(moveOffset, moveOffset) }

				onDrawBehind {
					// draw background
					drawRect(color = backgroundColor)

					// draw full sized qr background
					if (showQRBackground) {
						drawDataBlocks(
							blocks = blocks,
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
						roundness = roundness,
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
			},
	)
}

@Preview
@Composable
private fun QRTemplateHalfTonePreview() = QRForgeTheme {
	QRTemplateHalfTone(
		model = GeneratorPreviewFake.FAKE_GENERATED_UI_MODEL,
		modifier = Modifier.size(400.dp)
	)
}