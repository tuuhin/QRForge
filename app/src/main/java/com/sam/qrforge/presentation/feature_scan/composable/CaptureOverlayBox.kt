package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CameraCaptureOverlayBox(
	modifier: Modifier = Modifier,
	curveRadius: CornerSize = CornerSize(20.dp),
	borderStroke: BorderStroke = BorderStroke(3.dp, Color.White),
) {

	Box(
		modifier = modifier
			.size(200.dp)
			.drawBehind {
				drawCaptureBox(
					extraLineLength = size.width * .1f,
					curveRadius = curveRadius,
					borderStroke = borderStroke,
				)
			},
	)
}

private fun DrawScope.drawCaptureBox(
	extraLineLength: Float,
	curveRadius: CornerSize = CornerSize(40.dp),
	borderStroke: BorderStroke = BorderStroke(2.dp, Color.White),
) {
	val radius = curveRadius.toPx(size, this)

	val rectSize = Offset(radius, radius)


	val curvedPathTopLeft = Path().apply {
		reset()
		moveTo(0f, radius + extraLineLength)
		lineTo(0f, radius)
		addArc(
			oval = Rect(center = rectSize, radius = radius),
			startAngleDegrees = 180f,
			sweepAngleDegrees = 90f
		)
		lineTo(radius + extraLineLength, 0f)
	}

	val curvedPathBottomLeft = Path().apply {
		reset()
		moveTo(0f, size.height - (radius + extraLineLength))
		lineTo(0f, size.height - radius)
		addArc(
			oval = Rect(center = Offset(radius, size.height - radius), radius),
			startAngleDegrees = 90f,
			sweepAngleDegrees = 90f
		)
		moveTo(radius, size.height)
		lineTo(radius + extraLineLength, size.height)
	}

	val curvedPathTopRight = Path().apply {
		reset()
		moveTo(size.width - (radius + extraLineLength), 0f)
		lineTo(size.width - radius, 0f)
		addArc(
			oval = Rect(center = Offset(size.width - radius, radius), radius = radius),
			startAngleDegrees = 270f,
			sweepAngleDegrees = 90f
		)
		lineTo(size.width, radius + extraLineLength)
	}

	val curvedPathBottomRight = Path().apply {
		reset()
		moveTo(size.width, size.height - (radius + extraLineLength))
		lineTo(size.width, size.height - radius)
		addArc(
			oval = Rect(
				center = Offset(size.width - radius, size.height - radius),
				radius = radius
			),
			startAngleDegrees = 0f,
			sweepAngleDegrees = 90f
		)
		lineTo(size.width - (radius + extraLineLength), size.height)
	}


	drawPath(
		path = curvedPathBottomLeft,
		brush = borderStroke.brush,
		style = Stroke(
			width = borderStroke.width.toPx(),
			cap = StrokeCap.Round
		),
	)
	drawPath(
		path = curvedPathTopLeft,
		brush = borderStroke.brush,
		style = Stroke(
			width = borderStroke.width.toPx(),
			cap = StrokeCap.Round
		)
	)
	drawPath(
		path = curvedPathTopRight,
		brush = borderStroke.brush,
		style = Stroke(
			width = borderStroke.width.toPx(),
			cap = StrokeCap.Round
		)
	)

	drawPath(
		path = curvedPathBottomRight,
		brush = borderStroke.brush,
		style = Stroke(
			width = borderStroke.width.toPx(),
			cap = StrokeCap.Round
		)
	)

}