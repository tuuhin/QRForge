package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionMinimal

@Composable
fun QREditOptionMinimalistic(
	modifier: Modifier = Modifier,
	onDecorationChange: (QRDecorationOptionMinimal) -> Unit,
	decoration: QRDecorationOptionMinimal = QRDecorationOptionMinimal(),
	sliderColors: SliderColors = SliderDefaults.colors(),
	switchColors: SwitchColors = SwitchDefaults.colors(),
	titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
	bodyStyle: TextStyle = MaterialTheme.typography.bodySmall,
	titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		QREditBlockRoundness(
			roundness = decoration.roundness,
			onRoundnessChange = { roundness -> onDecorationChange(decoration.copy(roundness = roundness)) },
			titleStyle = titleStyle,
			titleColor = titleColor,
			bodyStyle = bodyStyle,
			bodyColor = bodyColor,
			sliderColors = sliderColors,
		)
		QREditBlockContentMargin(
			contentMargin = decoration.contentMargin,
			onMarginChange = { margin -> onDecorationChange(decoration.copy(contentMargin = margin)) },
			titleStyle = titleStyle,
			titleColor = titleColor,
			bodyStyle = bodyStyle,
			bodyColor = bodyColor,
			sliderColors = sliderColors,
		)
		QREditBlockBitsSize(
			bitsSizeMultiplier = decoration.bitsSizeMultiplier,
			onBitsMultiplierChange = { onDecorationChange(decoration.copy(bitsSizeMultiplier = it)) },
			titleStyle = titleStyle,
			titleColor = titleColor,
			bodyStyle = bodyStyle,
			bodyColor = bodyColor,
			sliderColors = sliderColors,
		)
		QREditBlockFinderShape(
			isShapeDiamond = decoration.isDiamond,
			onChangeShape = { isDiamond -> onDecorationChange(decoration.copy(isDiamond = isDiamond)) },
			titleStyle = titleStyle,
			titleColor = titleColor,
			bodyStyle = bodyStyle,
			bodyColor = bodyColor,
			switchColors = switchColors,
		)
	}
}