package com.sam.qrforge.presentation.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRContentTypeChip(
	type: QRDataType,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
	shape: Shape = MaterialTheme.shapes.large,
	contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
) {
	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier,
	) {
		Row(
			modifier = Modifier.padding(contentPadding),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Icon(
				painter = type.painter,
				contentDescription = type.string,
				modifier = Modifier.size(20.dp)
			)
			Text(
				text = type.string,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}


@PreviewLightDark
@Composable
private fun QRContentTypeChipPreview() = QRForgeTheme {
	QRContentTypeChip(type = QRDataType.TYPE_GEO)
}