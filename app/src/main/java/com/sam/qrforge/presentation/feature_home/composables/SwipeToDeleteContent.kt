package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@Composable
fun SwipeToDeleteContent(
	progress: () -> Float,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.errorContainer,
	contentColor: Color = contentColorFor(containerColor),
	shape: Shape = MaterialTheme.shapes.medium,
) {
	val coercedRatio = { (progress() - .01f).coerceIn(0f..1f) }

	Box(
		modifier = modifier
			.drawWithContent {
				val ratio = coercedRatio()
				val size = Size(size.width * ratio, size.height)
				val outline = shape.createOutline(size, layoutDirection, this)
				drawOutline(outline = outline, color = containerColor)
				if (ratio > .09f) {
					drawContent()
				}
			},
		contentAlignment = Alignment.CenterStart
	) {
		Icon(
			painter = painterResource(R.drawable.ic_delete),
			contentDescription = "Delete Action",
			tint = contentColor, modifier = Modifier.padding(12.dp)
		)
	}
}
