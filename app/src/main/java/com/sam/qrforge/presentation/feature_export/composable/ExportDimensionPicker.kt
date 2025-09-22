package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.presentation.common.mappers.localeString
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun ExportDimensionPicker(
	onDimensionChange: (ExportDimensions) -> Unit,
	modifier: Modifier = Modifier,
	selected: ExportDimensions = ExportDimensions.Medium,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
	shape: Shape = MaterialTheme.shapes.large,
	contentPadding: PaddingValues = PaddingValues(16.dp),
) {
	Surface(
		color = containerColor,
		shape = shape,
		modifier = modifier
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = stringResource(R.string.dimension_option_title),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.secondary
				)
				Surface(
					color = MaterialTheme.colorScheme.tertiary,
					contentColor = MaterialTheme.colorScheme.onTertiary,
					shape = MaterialTheme.shapes.extraLarge,
				) {
					Text(
						text = "${selected.sizeInPx} x ${selected.sizeInPx}",
						style = MaterialTheme.typography.labelMedium,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
					)
				}
			}
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ExportDimensions.entries.forEach { option ->
					FilterChip(
						selected = option == selected,
						onClick = { onDimensionChange(option) },
						label = { Text(text = option.localeString) },
						shape = MaterialTheme.shapes.large,
						border = FilterChipDefaults.filterChipBorder(
							enabled = true,
							selected = option == selected,
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

@PreviewLightDark
@Composable
private fun ExportDimensionsPickerPreview() = QRForgeTheme {
	ExportDimensionPicker(onDimensionChange = {})
}