package com.sam.qrforge.presentation.feature_create.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.composables.QREditOptionBasic
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.models.QRTemplateOption

@Composable
fun EditQRDecoration(
	onDecorationChange: (QRDecorationOption) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOptionBasic(),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(12.dp),
) {
	Surface(
		shape = shape,
		color = containerColor,
		modifier = modifier,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(contentPadding),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text(
				text = "Edit",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
			AnimatedContent(
				targetState = decoration.templateType,
				contentAlignment = Alignment.TopCenter,
			) { temp ->
				when (temp) {
					QRTemplateOption.BASIC -> {
						val localDecoration = (decoration as? QRDecorationOptionBasic)
							?: QRDecorationOptionBasic()
						QREditOptionBasic(
							onDecorationChange = onDecorationChange,
							decoration = localDecoration
						)
					}

					else -> {}
				}
			}
		}
	}
}