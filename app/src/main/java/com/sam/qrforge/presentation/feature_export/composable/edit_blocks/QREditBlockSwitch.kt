package com.sam.qrforge.presentation.feature_export.composable.edit_blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun QREditBlockSwitch(
	title: String,
	isChecked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	description: String? = null,
	switchColors: SwitchColors = SwitchDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
	) {
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = title,
				style = titleStyle,
				color = titleColor
			)
			description?.let {
				Text(
					text = description,
					style = bodyStyle,
					color = bodyColor
				)
			}
		}
		Switch(
			checked = isChecked,
			onCheckedChange = { isChecked -> onCheckedChange(isChecked) },
			colors = switchColors
		)
	}
}
