package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun FaultyQRWarningCard(
	showFaultyQRWarning: Boolean,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.tertiary,
	contentColor: Color = MaterialTheme.colorScheme.onTertiary,
	shape: Shape = MaterialTheme.shapes.large,
) {
	AnimatedVisibility(
		visible = showFaultyQRWarning,
		enter = expandVertically() + fadeIn(),
		exit = shrinkVertically() + fadeOut()
	) {
		Surface(
			color = containerColor,
			contentColor = contentColor,
			shape = shape,
			modifier = modifier
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 10.dp, vertical = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					painter = painterResource(R.drawable.ic_warning_filled),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onTertiary,
					modifier = Modifier.padding(8.dp)
				)
				Text(
					text = stringResource(R.string.faulty_qr_warning_text),
					style = MaterialTheme.typography.labelLarge,
					modifier = Modifier.weight(1f)
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun FaultyQRWarningCardPreview() = QRForgeTheme {
	FaultyQRWarningCard(showFaultyQRWarning = true)
}