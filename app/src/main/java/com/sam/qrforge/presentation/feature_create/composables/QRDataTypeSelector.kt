package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.QRDataType
import com.sam.qrforge.presentation.common.composables.painter
import com.sam.qrforge.presentation.common.composables.string
import com.sam.qrforge.presentation.common.utils.sharedTransitionSkipChildSize
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun QRDataTypeSelector(
	selectedType: QRDataType,
	onSelectType: (QRDataType) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		Text(
			text = stringResource(R.string.select_qr_format_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
		)
		Text(
			text = stringResource(R.string.select_qr_format_desc),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(10.dp))
		FlowRow(
			horizontalArrangement = Arrangement.Center,
			verticalArrangement = Arrangement.spacedBy(6.dp),
			maxItemsInEachRow = 4,
			modifier = Modifier
				.fillMaxWidth()
				.sharedTransitionSkipChildSize(),
		) {
			QRDataType.entries.forEach { type ->
				QRDataTypeCard(
					type = type,
					onClick = { onSelectType(type) },
					isSelected = type == selectedType,
					modifier = Modifier.padding(horizontal = 6.dp)
				)
			}
		}
	}
}

@Composable
private fun QRDataTypeCard(
	type: QRDataType,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	isSelected: Boolean = false,
	shape: Shape = MaterialTheme.shapes.extraLarge,
	unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	onUnSelectedContainer: Color = MaterialTheme.colorScheme.secondary,
	selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
	onSelectedContainer: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
	val containerColor by animateColorAsState(if (isSelected) selectedContainerColor else unselectedContainerColor)
	val contentColor by animateColorAsState(if (isSelected) onSelectedContainer else onUnSelectedContainer)

	Surface(
		onClick = onClick,
		color = containerColor,
		contentColor = contentColor,
		shape = shape,
		border = if (isSelected) BorderStroke(2.dp, onSelectedContainer) else null,
		modifier = modifier,
	) {
		Box(
			modifier = Modifier
				.padding(vertical = 10.dp)
				.defaultMinSize(minWidth = 72.dp, minHeight = 72.dp),
			contentAlignment = Alignment.Center,
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(
					painter = type.painter,
					contentDescription = type.string,
					modifier = Modifier.size(20.dp),
				)
				Text(
					text = type.string,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.SemiBold
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun QRDataTypeSelectorPreview() = QRForgeTheme {
	Surface {
		QRDataTypeSelector(
			selectedType = QRDataType.TYPE_TEXT,
			onSelectType = {},
			modifier = Modifier.padding(10.dp)
		)
	}
}