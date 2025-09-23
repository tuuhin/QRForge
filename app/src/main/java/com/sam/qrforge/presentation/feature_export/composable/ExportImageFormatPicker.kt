package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.presentation.common.mappers.localeString

@Composable
fun ExportMimeTypePicker(
	onFormatChange: (ImageMimeTypes) -> Unit,
	modifier: Modifier = Modifier,
	selectedFormat: ImageMimeTypes = ImageMimeTypes.PNG,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
	shape: Shape = MaterialTheme.shapes.large,
	enabled: Boolean = true,
	contentPadding: PaddingValues = PaddingValues(16.dp),
) {
	Surface(
		color = containerColor,
		shape = shape,
		modifier = modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				text = stringResource(R.string.export_select_image_format_title),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.secondary
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ImageMimeTypes.entries.forEach { option ->
					FilterChip(
						selected = option == selectedFormat,
						onClick = { onFormatChange(option) },
						enabled = enabled,
						label = { Text(text = option.localeString) },
						shape = MaterialTheme.shapes.large,
						border = FilterChipDefaults.filterChipBorder(
							enabled = true,
							selected = option == selectedFormat,
							borderColor = MaterialTheme.colorScheme.outlineVariant
						),
						colors = FilterChipDefaults.filterChipColors(
							selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
							selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
						)
					)
				}
			}
		}
	}
}