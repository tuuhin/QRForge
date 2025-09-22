package com.sam.qrforge.presentation.common.templates

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform

fun DrawScope.drawFindersClassic(
	finders: List<Offset>,
	blockSize: Float,
	isDiamond: Boolean = false,
	color: Color = Color.Black,
	backgroundColor: Color = Color.White,
	fractionOffset: Offset = Offset.Zero,
	roundness: Float = 0f,
	scaleFactor: Float = 1f,
	isTransparent: Boolean = true,
	blendMode: BlendMode = BlendMode.SrcOver,
) {
	scale(scaleFactor) {
		finders.forEach { offset ->
			val baseSize = Size(blockSize, blockSize)
			val innerRectSize = baseSize.times(3f)
			val rectSize = baseSize.times(5f)
			val outerRectSize = baseSize.times(7f)

			val path1 = Path().apply {
				reset()
				addRoundRect(
					RoundRect(
						Rect(offset = offset, size = outerRectSize),
						cornerRadius = CornerRadius(
							outerRectSize.width * .5f * roundness,
							outerRectSize.height * .5f * roundness
						),
					)
				)
			}

			val path2 = Path().apply {
				reset()
				addRoundRect(
					RoundRect(
						Rect(offset + Offset(blockSize, blockSize), rectSize),
						cornerRadius = CornerRadius(
							rectSize.width * .5f * roundness,
							rectSize.height * .5f * roundness
						),
					)
				)
			}
			val path3 = Path().apply {
				addRoundRect(
					RoundRect(
						Rect(offset + Offset(blockSize * 2f, blockSize * 2f), innerRectSize),
						cornerRadius = CornerRadius(
							innerRectSize.width * .5f * roundness,
							innerRectSize.height * .5f * roundness
						),
					)
				)
			}

			withTransform(
				transformBlock = {
					val pivot = Offset(
						offset.x + outerRectSize.width * .5f,
						offset.y + outerRectSize.height * .5f
					)
					rotate(
						degrees = if (isDiamond) 45f else 0f,
						pivot = pivot
					)
					scale(
						scale = if (isDiamond) .7f else 1f,
						pivot = pivot
					)
					translate(
						left = blockSize * .5f * fractionOffset.x,
						top = blockSize * .5f * fractionOffset.y,
					)
				},
			) {
				if (isTransparent) {
					val finalPath = Path().apply {
						op(
							path1 = path1,
							path2 = path2,
							operation = PathOperation.Difference,
						)
						addPath(path3)
					}
					drawPath(path = finalPath, color = color, blendMode = blendMode)
				} else {
					drawPath(path = path1, color = color)
					drawPath(path = path2, color = backgroundColor)
					drawPath(path = path3, color = color)
				}

			}
		}
	}
}

fun DrawScope.drawFindersMinimalistic(
	finders: List<Offset>,
	blockSize: Float,
	isDiamond: Boolean = false,
	color: Color = Color.Black,
	fractionOffset: Offset = Offset.Zero,
	roundness: Float = 0f,
	scaleFactor: Float = 1f,
	blendMode: BlendMode = BlendMode.SrcOver,
) {
	scale(scaleFactor) {
		finders.forEach { offset ->
			val baseSize = Size(blockSize, blockSize)
			val innerRectSize = baseSize.times(3f)
			val outerRectSize = baseSize.times(7f)


			val path3 = Path().apply {

				for (i in 0..3) {
					val currentOffset: Offset = when (i) {
						0 -> offset + Offset(blockSize * 3f, 0f)
						1 -> offset + Offset(blockSize * 3f, blockSize * 6f)
						2 -> offset + Offset(0f, blockSize * 3f)
						3 -> offset + Offset(blockSize * 6f, blockSize * 3f)
						else -> continue
					}

					addRoundRect(
						RoundRect(
							rect = Rect(currentOffset, baseSize),
							cornerRadius = CornerRadius(
								baseSize.width * .5f * roundness,
								baseSize.height * .5f * roundness
							),
						)
					)
				}

				// inner rect
				addRoundRect(
					RoundRect(
						Rect(offset + Offset(blockSize * 2f, blockSize * 2f), innerRectSize),
						cornerRadius = CornerRadius(
							innerRectSize.width * .5f * roundness,
							innerRectSize.height * .5f * roundness
						),
					)
				)
			}

			withTransform(
				transformBlock = {
					val pivot = Offset(
						offset.x + outerRectSize.width * .5f,
						offset.y + outerRectSize.height * .5f
					)
					rotate(
						degrees = if (isDiamond) 45f else 0f,
						pivot = pivot
					)
					scale(
						scale = if (isDiamond) .7f else 1f,
						pivot = pivot
					)
					translate(
						left = outerRectSize.width * .25f * fractionOffset.x,
						top = outerRectSize.height * .25f * fractionOffset.y,
					)
				},
			) {

				drawPath(path = path3, color = color, blendMode = blendMode)
			}
		}
	}
}

fun DrawScope.drawAlignmentBlocks(
	alignments: List<Offset>,
	blockSize: Float,
	isDiamond: Boolean = false,
	color: Color = Color.Black,
	backgroundColor: Color = Color.White,
	fractionOffset: Offset = Offset.Zero,
	roundness: Float = 0f,
	scaleFactor: Float = 1f,
	isTransparent: Boolean = true,
	blendMode: BlendMode = BlendMode.SrcOver,
) {
	scale(scaleFactor) {
		alignments.forEach { offset ->
			val innerRectSize = Size(blockSize, blockSize)
			val rectSize = innerRectSize.times(3f)
			val outerRectSize = innerRectSize.times(5f)

			val path1 = Path().apply {
				reset()
				addRoundRect(
					RoundRect(
						Rect(offset = offset, size = outerRectSize),
						cornerRadius = CornerRadius(
							outerRectSize.width * .5f * roundness,
							outerRectSize.height * .5f * roundness
						),
					)
				)
			}

			val path2 = Path().apply {
				reset()
				addRoundRect(
					RoundRect(
						Rect(offset + Offset(blockSize, blockSize), rectSize),
						cornerRadius = CornerRadius(
							rectSize.width * .5f * roundness,
							rectSize.height * .5f * roundness
						),
					)
				)
			}
			val path3 = Path().apply {
				addRoundRect(
					RoundRect(
						Rect(offset + Offset(blockSize * 2f, blockSize * 2f), innerRectSize),
						cornerRadius = CornerRadius(
							innerRectSize.width * .5f * roundness,
							innerRectSize.height * .5f * roundness
						),
					)
				)
			}

			withTransform(
				transformBlock = {
					val pivot = Offset(
						offset.x + outerRectSize.width * .5f,
						offset.y + outerRectSize.height * .5f
					)
					rotate(
						degrees = if (isDiamond) 45f else 0f,
						pivot = pivot
					)
					scale(
						scale = if (isDiamond) .7f else 1f,
						pivot = pivot
					)
					translate(
						left = outerRectSize.width * .25f * fractionOffset.x,
						top = outerRectSize.height * .25f * fractionOffset.y,
					)
				},
			) {
				if (isTransparent) {
					val finalPath = Path().apply {
						op(
							path1 = path1,
							path2 = path2,
							operation = PathOperation.Difference,
						)
						addPath(path3)
					}
					drawPath(path = finalPath, color = color, blendMode = blendMode)
				} else {
					drawPath(path = path1, color = color)
					drawPath(path = path2, color = backgroundColor)
					drawPath(path = path3, color = color)
				}
			}
		}
	}
}

fun DrawScope.drawDataBlocks(
	blocks: List<Offset>,
	blockSize: Float,
	scaleFactor: Float = 1f,
	fractionOffset: Offset = Offset.Zero,
	roundness: Float = 0f,
	multiplier: Float = 1f,
	isDiamond: Boolean = false,
	bitsColor: Color = Color.Black,
	blendMode: BlendMode = BlendMode.SrcOver,
) {
	val finalSize = Size(blockSize * multiplier, blockSize * multiplier)
	val halfBlock = blockSize * .5f

	scale(scaleFactor) {
		blocks.forEach { offset ->
			withTransform(
				transformBlock = {
					val pivot = Offset(
						offset.x + finalSize.width * .5f,
						offset.y + finalSize.height * .5f
					)
					rotate(
						degrees = if (isDiamond) 45f else 0f,
						pivot = pivot
					)
					scale(
						scale = if (isDiamond) .7f else 1f,
						pivot = pivot
					)
					translate(
						left = halfBlock * fractionOffset.x,
						top = halfBlock * fractionOffset.y,
					)
				},
			) {
				drawRoundRect(
					color = bitsColor,
					topLeft = offset,
					cornerRadius = CornerRadius(
						halfBlock * multiplier * roundness,
						halfBlock * multiplier * roundness
					),
					size = finalSize,
					blendMode = blendMode,
				)
			}
		}
	}
}

fun DrawScope.drawTimingBlocks(
	blocks: List<Offset>,
	blockSize: Float,
	scaleFactor: Float = 1f,
	fractionOffset: Offset = Offset.Zero,
	roundness: Float = 0f,
	multiplier: Float = 1f,
	isDiamond: Boolean = false,
	evenBitsColor: Color = Color.Black,
	oddBitsColor: Color = Color.White,
	blendMode: BlendMode = BlendMode.SrcOver,
) {
	scale(scaleFactor) {
		blocks.forEachIndexed { idx, offset ->
			val finalSize = Size(
				blockSize * multiplier,
				blockSize * multiplier
			)
			withTransform(
				transformBlock = {
					val pivot = Offset(
						offset.x + finalSize.width * .5f,
						offset.y + finalSize.height * .5f
					)
					rotate(
						degrees = if (isDiamond) 45f else 0f,
						pivot = pivot
					)
					scale(
						scale = if (isDiamond) .7f else 1f,
						pivot = pivot
					)
					translate(
						left = blockSize * .5f * fractionOffset.x,
						top = blockSize * .5f * fractionOffset.y,
					)
				},
			) {
				drawRoundRect(
					color = if (idx % 2 == 0) evenBitsColor else oddBitsColor,
					topLeft = offset,
					cornerRadius = CornerRadius(
						blockSize * .5f * multiplier * roundness,
						blockSize * .5f * multiplier * roundness
					),
					size = finalSize,
					blendMode = blendMode,
				)
			}
		}
	}
}

fun DrawScope.drawFrame(
	totalMargin: Float,
	blockSize: Float,
	frameColor: Color,
	roundness: Float = 0f,
) {
	val path = Path().apply {
		reset()
		// top left
		moveTo(totalMargin * .5f, size.height * .5f - blockSize * 5f)
		lineTo(totalMargin * .5f, totalMargin * .5f + blockSize * 2f)
		quadraticTo(
			totalMargin * .5f,
			totalMargin * .5f,
			totalMargin * .5f + blockSize * 2f,
			totalMargin * .5f
		)
		lineTo(
			size.width * .5f - blockSize * 5f,
			totalMargin * .5f
		)
		// top right
		moveTo(
			size.width * .5f + blockSize * 5f,
			totalMargin * .5f
		)
		lineTo(
			size.width - (totalMargin * .5f + blockSize * 2f),
			totalMargin * .5f
		)
		quadraticTo(
			size.width - totalMargin * .5f,
			totalMargin * .5f,
			size.width - totalMargin * .5f,
			totalMargin * .5f + blockSize * 2f
		)
		lineTo(
			size.width - totalMargin * .5f,
			size.height * .5f - blockSize * 5f
		)
		//bottom right
		moveTo(
			size.width - totalMargin * .5f,
			size.height * .5f + blockSize * 5f
		)
		lineTo(
			size.width - totalMargin * .5f,
			size.height - (totalMargin * .5f + blockSize * 2f)
		)
		quadraticTo(
			size.width - totalMargin * .5f,
			size.height - totalMargin * .5f,
			size.width - (totalMargin * .5f + blockSize * 2f),
			size.height - totalMargin * .5f
		)
		lineTo(
			size.width * .5f + blockSize * 5f,
			size.height - totalMargin * .5f
		)
		// bottom left
		moveTo(
			size.width * .5f - blockSize * 5f,
			size.height - totalMargin * .5f
		)
		lineTo(
			totalMargin * .5f + blockSize * 2f,
			size.height - totalMargin * .5f
		)
		quadraticTo(
			totalMargin * .5f,
			size.height - totalMargin * .5f,
			totalMargin * .5f,
			size.height - (totalMargin * .5f + blockSize * 2f)
		)
		lineTo(
			totalMargin * .5f,
			size.height * .5f + blockSize * 5f
		)
	}
	drawPath(
		path = path,
		color = frameColor,
		style = Stroke(
			width = blockSize,
			cap = if (roundness == 0f) StrokeCap.Butt else StrokeCap.Round
		)
	)
}