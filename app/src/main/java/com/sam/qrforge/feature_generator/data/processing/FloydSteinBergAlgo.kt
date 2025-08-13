package com.sam.qrforge.feature_generator.data.processing

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.scale
import kotlin.math.roundToInt

object FloydSteinBergAlgo {

	private const val RED_LUMINANCE = 0.299f
	private const val GREEN_LUMINANCE = 0.587f
	private const val BLUE_LUMINANCE = 0.114f

	fun runAlgo(
		bitmap: ImageBitmap,
		finalImageSize: IntSize,
		blockSize: Int,
		blackMaskColor: Int = Color.BLACK,
		whiteMaskColor: Int = Color.WHITE,
	): Bitmap {
		//scaled image
		val newBitMap = bitmap.asAndroidBitmap().scale(finalImageSize.width, finalImageSize.height)
		val pixels = toIntArray(newBitMap.asImageBitmap())

		runAlgo(pixels = pixels, width = finalImageSize.width, finalImageSize.height)

		return downsampleMax(
			pixels = pixels,
			width = finalImageSize.width,
			height = finalImageSize.height,
			blockSize = blockSize,
			blackMaskColor = blackMaskColor,
			whiteMaskColor = whiteMaskColor,
		)
	}

	private fun runAlgo(pixels: IntArray, width: Int, height: Int) {
		for (y in 0..<height) {
			for (x in 0..<width) {
				val i = (y * width + x)

				val currentPixelInt = pixels[i]

				val alpha = (currentPixelInt shr 24) and 0xFF
				val red = (currentPixelInt shr 16) and 0xFF
				val green = (currentPixelInt shr 8) and 0xFF
				val blue = (currentPixelInt and 0xFF)

				if (alpha == 0) continue

				// Convert to grayscale using luminance formula
				val oldPixelLuma =
					(red * RED_LUMINANCE + green * GREEN_LUMINANCE + blue * BLUE_LUMINANCE).roundToInt()

				// Quantize to pure black (0) or pure white (255)
				val newPixelLuma = if (oldPixelLuma < 128) 0 else 255

				pixels[i] = Color.argb(alpha, newPixelLuma, newPixelLuma, newPixelLuma)

				val error = oldPixelLuma - newPixelLuma

				if (x + 1 < width) propagateError(pixels, i + 1, error, 7f / 16f)
				if (y + 1 < height && x - 1 >= 0)
					propagateError(pixels, i + width - 1, error, 3f / 16f)
				if (y + 1 < height)
					propagateError(pixels, i + width, error, 5f / 16f)
				if (y + 1 < height && x + 1 < width)
					propagateError(pixels, i + width + 1, error, 1f / 16f)
			}
		}
	}

	private fun propagateError(pixels: IntArray, targetPixel: Int, error: Int, weight: Float) {
		if (targetPixel < 0 || targetPixel >= pixels.size) return

		val pixel = pixels[targetPixel]
		val alpha = (pixel shr 24) and 0xFF
		val red = (pixel shr 16) and 0xFF
		val green = (pixel shr 8) and 0xFF
		val blue = (pixel and 0xFF)

		val target = (red * RED_LUMINANCE + green * GREEN_LUMINANCE + blue * BLUE_LUMINANCE)
		val errorPixel = (target + error * weight).roundToInt().coerceIn(0, 255)
		pixels[targetPixel] = Color.argb(alpha, errorPixel, errorPixel, errorPixel)

	}

	private fun downsampleMax(
		pixels: IntArray,
		width: Int,
		height: Int,
		blockSize: Int = 10,
		blackMaskColor: Int = Color.BLACK,
		whiteMaskColor: Int = Color.WHITE,
	): Bitmap {
		for (yOut in 0..<height step blockSize) {
			for (xOut in 0..<width step blockSize) {

				var whiteCount = 0
				var blackCount = 0
				var alphaSum = 0
				var pixelCount = 0

				val yStart = yOut
				val xStart = xOut

				val yEnd = minOf(yStart + blockSize, height)
				val xEnd = minOf(xStart + blockSize, width)


				for (yIn in yStart..<yEnd) {
					for (xIn in xStart..<xEnd) {
						val index = (yIn * width + xIn)
						val alpha = (pixels[index] shr 24) and 0xFF
						val gray = (pixels[index] shr 16) and 0xFF

						if (gray >= 128) whiteCount++ else blackCount++

						alphaSum += alpha
						pixelCount++
					}
				}

				if (pixelCount == 0) continue

				val isWhite = whiteCount > blackCount

				val maskRed = if (isWhite) (whiteMaskColor shr 16) and 0xFF
				else (blackMaskColor shr 16) and 0xFF

				val maskGreen = if (isWhite) (whiteMaskColor shr 8) and 0xFF
				else (blackMaskColor shr 8) and 0xFF

				val maskBlue = if (isWhite) whiteMaskColor and 0xFF else blackMaskColor and 0xFF

				val finalAlpha = if (alphaSum / pixelCount >= 127) 255 else 0
				val blockColor = Color.argb(finalAlpha, maskRed, maskGreen, maskBlue)

				for (yIn in yStart..<yEnd) {
					for (xIn in xStart..<xEnd) {
						val index = (yIn * width + xIn)
						pixels[index] = blockColor
					}
				}
			}
		}
		return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888)
	}

	private fun toIntArray(bitmap: ImageBitmap): IntArray {
		val composeBitMap = bitmap.asAndroidBitmap()
		val pixels = IntArray(composeBitMap.width * composeBitMap.height).apply {
			composeBitMap.getPixels(
				this, 0, composeBitMap.width, 0, 0,
				composeBitMap.width, composeBitMap.height
			)
		}
		return pixels
	}
}