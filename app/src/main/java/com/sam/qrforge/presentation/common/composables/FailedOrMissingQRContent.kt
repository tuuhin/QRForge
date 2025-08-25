package com.sam.qrforge.presentation.common.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@Composable
fun FailedOrMissingQRContent(
	modifier: Modifier = Modifier,
	isError: Boolean = false,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	stroke: BorderStroke = BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary)
) {
	Box(
		modifier = modifier
			.size(120.dp, 80.dp)
			.drawWithContent {
				drawRoundRect(
					brush = stroke.brush,
					cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
					style = Stroke(
						width = stroke.width.toPx(),
						pathEffect = PathEffect.dashPathEffect(
							intervals = floatArrayOf(40f, 40f)
						)
					)
				)
				drawContent()
			},
		contentAlignment = Alignment.Center
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(6.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				painter = painterResource(R.drawable.ic_content_missing),
				contentDescription = "Missing content",
				tint = contentColor,
				modifier = Modifier.size(72.dp)
			)
			Text(
				text = if (!isError) "No content yet" else "Cannot render QR",
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.Bold,
				color = contentColor,
			)
		}
	}
}