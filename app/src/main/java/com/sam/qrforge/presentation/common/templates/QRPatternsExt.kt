package com.sam.qrforge.presentation.common.templates

import androidx.compose.ui.geometry.Offset
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel

private val ALIGNMENT_PATTERN_CENTERS = mapOf(
	1 to emptyList(),
	2 to listOf(6, 18),
	3 to listOf(6, 22),
	4 to listOf(6, 26),
	5 to listOf(6, 30),
	6 to listOf(6, 34),
	7 to listOf(6, 22, 38),
	8 to listOf(6, 24, 42),
	9 to listOf(6, 26, 46),
	10 to listOf(6, 28, 50),
	11 to listOf(6, 30, 54),
	12 to listOf(6, 32, 58),
	13 to listOf(6, 34, 62),
	14 to listOf(6, 26, 46, 66),
	15 to listOf(6, 26, 48, 70),
	16 to listOf(6, 26, 50, 74),
	17 to listOf(6, 30, 54, 78),
	18 to listOf(6, 30, 56, 82),
	19 to listOf(6, 30, 58, 86),
	20 to listOf(6, 34, 62, 90),
	21 to listOf(6, 28, 50, 72, 94),
	22 to listOf(6, 26, 50, 74, 98),
	23 to listOf(6, 30, 54, 78, 102),
	24 to listOf(6, 28, 54, 80, 106),
	25 to listOf(6, 32, 58, 84, 110),
	26 to listOf(6, 30, 58, 86, 114),
	27 to listOf(6, 34, 62, 90, 118),
	28 to listOf(6, 26, 50, 74, 98, 122),
	29 to listOf(6, 30, 54, 78, 102, 126),
	30 to listOf(6, 26, 52, 78, 104, 130),
	31 to listOf(6, 30, 56, 82, 108, 134),
	32 to listOf(6, 34, 60, 86, 112, 138),
	33 to listOf(6, 30, 58, 86, 114, 142),
	34 to listOf(6, 34, 62, 90, 118, 146),
	35 to listOf(6, 30, 54, 78, 102, 126, 150),
	36 to listOf(6, 24, 50, 76, 102, 128, 154),
	37 to listOf(6, 28, 54, 80, 106, 132, 158),
	38 to listOf(6, 32, 58, 84, 110, 136, 162),
	39 to listOf(6, 26, 54, 82, 110, 138, 166),
	40 to listOf(6, 30, 58, 86, 114, 142, 170)
)

private fun GeneratedQRUIModel.isNotFinderPatternOrMargin(posX: Int, posY: Int): Boolean {
	val isHorizontalMargin = posX !in margin..<widthInBlocks - margin
	val isVerticalMargin = posY !in margin..<heightInBlocks - margin

	if (isHorizontalMargin && isVerticalMargin) return false

	val inTopLeft = posX <= 7 + margin && posY <= 7 + margin
	val inTopRight = posX >= (widthInBlocks - margin - 7) && posY - margin <= 7
	val inBottomLeft = posX <= 7 + margin && posY >= (heightInBlocks - margin - 7)
	return !inTopLeft && !inTopRight && !inBottomLeft
}

fun GeneratedQRUIModel.finderOffsets(blockSize: Float): List<Offset> {
	return buildList {
		add(Offset(margin * blockSize, margin * blockSize))
		add(Offset((widthInBlocks - 7 - margin) * blockSize, margin * blockSize))
		add(Offset(margin * blockSize, (widthInBlocks - 7 - margin) * blockSize))
	}
}

fun GeneratedQRUIModel.timingPatternOffsets(blockSize: Float): List<Offset> {
	return buildList {
		// width line
		for (x in margin + 8..<widthInBlocks - (margin + 8))
			add(Offset(x * blockSize, (margin + 6) * blockSize))
		// height line
		for (x in margin + 7..<heightInBlocks - (margin + 8))
			add(Offset((margin + 6) * blockSize, x * blockSize))
	}
}


fun GeneratedQRUIModel.alignmentPatternOffsets(blockSize: Float): List<Offset> {

	val centers = ALIGNMENT_PATTERN_CENTERS.getOrDefault(version, null)
		?: return emptyList()

	return centers.zip(centers)
		.filter { isNotFinderPatternOrMargin(it.first, it.second) }
		.map { Offset(it.first * blockSize, it.second * blockSize) }
}

fun GeneratedQRUIModel.dataBitsOffset(blockSize: Float): List<Offset> {
	return buildList {
		for ((colIdx, column) in qrMatrix.matrix.withIndex()) {
			for ((rowIdx, blockIsBlack) in column.withIndex()) {
				if (colIdx !in enclosingRect.top..<enclosingRect.bottom) continue
				if (rowIdx !in enclosingRect.left..<enclosingRect.right) continue
				// ensure position inside the bounding rect
				val isNotFinder = isNotFinderPatternOrMargin(rowIdx, colIdx)
				if (blockIsBlack &&isNotFinder) {
					add(Offset(rowIdx * blockSize, colIdx * blockSize))
				}
			}
		}
	}
}