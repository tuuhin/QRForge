package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRDecorationOption.QRDecorationOptionBasic
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.feature_home.composables.QREditOptionBasic
import com.sam.qrforge.presentation.feature_home.composables.QREditOptionMinimalistic

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
				transitionSpec = {
					slideInVertically(
						animationSpec = tween(220, delayMillis = 90, easing = EaseIn)
					) + scaleIn(
						initialScale = 0.92f,
						animationSpec = tween(220, delayMillis = 90)
					) togetherWith
							slideOutVertically(
								animationSpec = tween(90, easing = EaseOut)
							) + scaleOut(animationSpec = tween(durationMillis = 90))
				}
			) { temp ->
				when (temp) {
					QRTemplateOption.BASIC -> {
						val decor = (decoration as? QRDecorationOptionBasic)
							?: QRDecorationOptionBasic()
						QREditOptionBasic(
							onDecorationChange = onDecorationChange,
							decoration = decor
						)
					}
					QRTemplateOption.MINIMALISTIC -> {
						val decor = (decoration as? QRDecorationOption.QRDecorationOptionMinimal)
								?: QRDecorationOption.QRDecorationOptionMinimal()

						QREditOptionMinimalistic(
							onDecorationChange = onDecorationChange,
							decoration = decor
						)
					}

					else -> {}
				}
			}
		}
	}
}