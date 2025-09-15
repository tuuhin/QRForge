package com.sam.qrforge.presentation.feature_scan.composable

import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun CaptureButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = CircleShape,
	baseColor: Color = Color.White,
	captureProgress: () -> Float = { 0f },
	isCapturing: Boolean = false,
	canReadProgress: Boolean = false,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	capturingIndicatorColor: Color = MaterialTheme.colorScheme.tertiary,
	content: @Composable () -> Unit,
) {

	val infiniteTransition = rememberInfiniteTransition(label = "Infinite transition")

	val boxSize by infiniteTransition.animateValue(
		initialValue = CaptureButtonDefaults.rippleBoxSizeStart,
		targetValue = if (enabled) CaptureButtonDefaults.rippleBoxSizeEnd
		else CaptureButtonDefaults.rippleBoxSizeStart,
		typeConverter = Dp.VectorConverter,
		animationSpec = infiniteRepeatable(
			animation = tween(1200, 400, EaseOut),
			repeatMode = RepeatMode.Reverse
		),
		label = "Ripple Indication"
	)

	val rippleAlpha by infiniteTransition.animateFloat(
		initialValue = if (enabled) .3f else .1f,
		targetValue = .1f,
		animationSpec = infiniteRepeatable(
			animation = tween(1200, 400, EaseInOutExpo),
			repeatMode = RepeatMode.Reverse
		),
		label = "Ripple colors"
	)

	val sweepAngle by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 360f,
		animationSpec = infiniteRepeatable(
			animation = tween(800, 400, EaseOut),
			repeatMode = RepeatMode.Restart
		),
		label = "Ripple colors"
	)


	Box(
		modifier = modifier.defaultMinSize(
			minWidth = CaptureButtonDefaults.rippleBoxSizeEnd,
			minHeight = CaptureButtonDefaults.rippleBoxSizeEnd
		),
		contentAlignment = Alignment.Center
	) {
		// ripple container
		Box(
			modifier = Modifier
				.matchParentSize()
				.drawBehind {
					if (!isCapturing)
						drawCircle(
							color = baseColor,
							radius = boxSize.toPx() / 2f,
							alpha = rippleAlpha
						)
				},
		)
		// clickable border box
		Box(
			modifier = Modifier
				.indication(
					interactionSource = interactionSource,
					indication = ripple()
				)
				.clip(shape)
				.clickable(
					role = Role.Button,
					onClick = onClick,
					enabled = enabled,
					interactionSource = interactionSource,
					indication = ripple(color = Color.LightGray)
				)
				.size(size = CaptureButtonDefaults.contentBoxSize)
				.drawBehind {

					val scale = .85f
					if (isCapturing) {
						val progress = if (canReadProgress) captureProgress() * 360f
						else sweepAngle

						drawArc(
							color = capturingIndicatorColor,
							startAngle = 270f,
							sweepAngle = progress.coerceIn(0f..360f),
							useCenter = false,
							style = Stroke(6.dp.toPx(), cap = StrokeCap.Round)
						)
					} else drawOutline(
						outline = shape.createOutline(size, layoutDirection, this),
						color = baseColor,
						style = Stroke(4.dp.toPx())
					)
					translate(
						left = size.width * .5f * (1 - scale),
						top = size.height * .5f * (1 - scale)
					) {
						drawOutline(
							outline = shape.createOutline(size.times(scale), layoutDirection, this),
							color = baseColor,
						)
					}
				},
			contentAlignment = Alignment.Center
		) {
			content()
		}
	}
}

private object CaptureButtonDefaults {
	val rippleBoxSizeStart = 68.dp
	val rippleBoxSizeEnd = 104.dp
	val contentBoxSize = 72.dp
}

@Preview
@Composable
private fun CaptureButtonNormalPreview() = QRForgeTheme {
	CaptureButton(
		onClick = { },
		modifier = Modifier.padding(10.dp)
	) {
		Icon(
			painter = painterResource(id = R.drawable.ic_scan),
			contentDescription = "Shutter",
			tint = Color.Black
		)
	}
}

@Preview
@Composable
private fun CaptureButtonAnimatingPreview() = QRForgeTheme {
	CaptureButton(
		onClick = { },
		enabled = false,
		isCapturing = true,
		canReadProgress = true,
		captureProgress = { .5f },
	) {
		Icon(
			painter = painterResource(id = R.drawable.ic_scan),
			contentDescription = "Shutter",
			tint = Color.Black
		)
	}
}