package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.QRTemplateOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.templates.QRTemplateLayered
import com.sam.qrforge.presentation.common.templates.QRTemplateMinimalistic

@Composable
fun QRMasterTemplate(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	graphicsLayer: (@Composable () -> GraphicsLayer)? = null,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
) {
	Box(
		modifier = modifier.defaultMinSize(minWidth = 120.dp, 120.dp),
	) {
		when (decoration.templateType) {
			QRTemplateOption.BASIC -> {
				val decor = decoration as? QRDecorationOption.QRDecorationOptionBasic
				QRTemplateBasic(
					model = model,
					decoration = decor ?: QRDecorationOption.QRDecorationOptionBasic(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.matchParentSize()
				)
			}

			QRTemplateOption.MINIMALISTIC -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionMinimal

				QRTemplateMinimalistic(
					model = model,
					decoration = type ?: QRDecorationOption.QRDecorationOptionMinimal(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.matchParentSize(),
				)
			}

			QRTemplateOption.COLOR_LAYERED -> {
				val type = decoration as? QRDecorationOption.QRDecorationOptionColorLayer
				QRTemplateLayered(
					model = model,
					decoration = type ?: QRDecorationOption.QRDecorationOptionColorLayer(),
					graphicsLayer = graphicsLayer,
					modifier = Modifier.matchParentSize()
				)
			}
		}
	}
}