package com.sam.qrforge.presentation.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlin.math.roundToInt

@Composable
fun QREditOptionBasic(
	modifier: Modifier = Modifier,
	onDecorationChange: (QRDecorationOptionBasic) -> Unit,
	decoration: QRDecorationOptionBasic = QRDecorationOptionBasic(),
) {

	val density = LocalDensity.current

	val contentMarginSimple = remember(decoration.contentMargin) {
		with(density) {
			val current = decoration.contentMargin.toPx()
			(current / 20.dp.toPx()).roundToInt() * 10f
		}
	}

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(
				text = "Roundness",
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(
				text = "Make all the points rounded",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Slider(
				value = decoration.roundness.coerceIn(0f..10f),
				onValueChange = { current -> onDecorationChange(decoration.copy(roundness = current)) },
				steps = 10,
				valueRange = 0f..10f
			)
		}
		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(
				text = "Margin",
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(
				text = "Add extra margin",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Slider(
				value = contentMarginSimple.coerceIn(0f..10f),
				onValueChange = { current ->
					val margin = with(density) { 2.dp * current }
					onDecorationChange(
						decoration.copy(contentMargin = margin)
					)
				},
				steps = 10,
				valueRange = 0f..10f
			)
		}

		Row(verticalAlignment = Alignment.CenterVertically) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = "Show Frame",
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = "Add an additional frame to QR.",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			Switch(
				checked = decoration.showFrame,
				onCheckedChange = { isChecked -> onDecorationChange(decoration.copy(showFrame = isChecked)) })
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = "Finder Shape",
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = "Finder shaped as diamond",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			Switch(
				checked = decoration.isDiamond,
				onCheckedChange = { isChecked -> onDecorationChange(decoration.copy(isDiamond = isChecked)) })
		}
	}

}


@PreviewLightDark
@Composable
private fun QREditOptionBasicPreview() = QRForgeTheme {
	Surface {
		QREditOptionBasic(
			onDecorationChange = {},
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp)
		)
	}
}