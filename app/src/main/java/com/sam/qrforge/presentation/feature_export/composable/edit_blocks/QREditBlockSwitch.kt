package com.sam.qrforge.presentation.feature_export.composable.edit_blocks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.copyBlockShape

@Composable
private fun QREditBlockSwitch(
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


@Composable
fun EditDecorationBooleanOptions(
	decoration: QRDecorationOption,
	onDecorationChange: (QRDecorationOption) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {

	val isBasicDecoration by remember(decoration) {
		derivedStateOf { decoration is QRDecorationOption.QRDecorationOptionBasic }
	}

	val isMinimalisticDecoration by remember(decoration) {
		derivedStateOf { decoration is QRDecorationOption.QRDecorationOptionMinimal }
	}

	val isLayeredDecoration by remember(decoration) {
		derivedStateOf { decoration is QRDecorationOption.QRDecorationOptionColorLayer }
	}

	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			AnimatedVisibility(
				visible = !isLayeredDecoration,
			) {
				QREditBlockSwitch(
					title = stringResource(R.string.qr_edit_property_finder_shape_diamond_title),
					description = stringResource(R.string.qr_edit_property_finder_shape_diamond_text),
					isChecked = decoration.isDiamond,
					onCheckedChange = { isDiamond ->
						val modified = decoration.copyBlockShape(isDiamond = isDiamond)
						onDecorationChange(modified)
					},
				)
			}
			AnimatedVisibility(
				visible = isBasicDecoration,
			) {
				if (decoration !is QRDecorationOption.QRDecorationOptionBasic) return@AnimatedVisibility
				QREditBlockSwitch(
					title = stringResource(R.string.qr_edit_property_show_frame_title),
					description = stringResource(R.string.qr_edit_property_show_frame_text),
					isChecked = decoration.showFrame,
					onCheckedChange = { show ->
						onDecorationChange(decoration.copy(showFrame = show))
					},
				)
			}
			AnimatedVisibility(
				visible = isMinimalisticDecoration,
			) {
				if (decoration !is QRDecorationOption.QRDecorationOptionMinimal) return@AnimatedVisibility
				QREditBlockSwitch(
					title = stringResource(R.string.qr_edit_property_show_background_title),
					description = stringResource(R.string.qr_edit_property_show_background_text),
					isChecked = decoration.showBackground,
					onCheckedChange = { show ->
						onDecorationChange(decoration.copy(showBackground = show))
					},
				)
			}
		}
	}
}