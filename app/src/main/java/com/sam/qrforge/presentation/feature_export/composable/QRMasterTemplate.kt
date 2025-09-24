package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.templates.QRTemplateLayered
import com.sam.qrforge.presentation.common.templates.QRTemplateMinimalistic

@Composable
fun QRMasterTemplate(
	model: GeneratedQRUIModel,
	modifier: Modifier = Modifier,
	captureLayer: CanvasCaptureLayer = CanvasCaptureLayer(),
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
) {
	Box(
		modifier = modifier.defaultMinSize(minWidth = 120.dp, 120.dp),
	) {
		when (decoration) {
			is QRDecorationOption.QRDecorationOptionBasic -> QRTemplateBasic(
				model = model,
				decoration = decoration,
				captureLayer = captureLayer,
				modifier = Modifier.matchParentSize()
			)

			is QRDecorationOption.QRDecorationOptionMinimal -> QRTemplateMinimalistic(
				model = model,
				decoration = decoration,
				captureLayer = captureLayer,
				modifier = Modifier.matchParentSize(),
			)

			is QRDecorationOption.QRDecorationOptionColorLayer -> QRTemplateLayered(
				model = model,
				decoration = decoration,
				captureLayer = captureLayer,
				modifier = Modifier.matchParentSize()
			)
		}
	}
}