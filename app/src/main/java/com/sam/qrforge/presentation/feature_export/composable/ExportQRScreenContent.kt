package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.CanvasCaptureLayer
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.feature_export.state.ExportQRScreenEvents

@Composable
fun ExportQRScreenContent(
	generatedQR: GeneratedQRUIModel,
	onEvent: (ExportQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	showFaultyQRWarning: Boolean = false,
	captureLayer: CanvasCaptureLayer = CanvasCaptureLayer(),
	contentPadding: PaddingValues = PaddingValues(12.dp),
) {
	Column(
		modifier = modifier.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(6.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		FaultyQRWarningCard(
			showFaultyQRWarning = showFaultyQRWarning,
			onDismissWarning = { onEvent(ExportQRScreenEvents.OnDismissWarning) },
			modifier = Modifier.fillMaxWidth()
		)
		Surface(
			color = MaterialTheme.colorScheme.surfaceContainerLow,
			shape = MaterialTheme.shapes.extraLarge
		) {
			QRMasterTemplate(
				model = generatedQR,
				decoration = decoration,
				captureLayer = captureLayer,
				backgroundFallback = MaterialTheme.colorScheme.surfaceContainerLow,
				modifier = Modifier.size(240.dp)
			)
		}
		Text(
			text = stringResource(R.string.select_template_title),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.secondary,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.qr_edit_option_internal_padding))
		)
		QRTemplatePicker(
			selectedTemplate = decoration.templateType,
			onTemplateChange = { onEvent(ExportQRScreenEvents.OnQRTemplateChange(it)) },
			containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
			contentPadding = PaddingValues(dimensionResource(R.dimen.sc_padding)),
			modifier = Modifier.fillMaxWidth(),
		)
		Text(
			text = stringResource(R.string.action_edit),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.secondary,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = dimensionResource(R.dimen.qr_edit_option_internal_padding))
		)
		EditQRDecorationOptions(
			decoration = decoration,
			onDecorationChange = { onEvent(ExportQRScreenEvents.OnDecorationChange(it)) },
			modifier = Modifier.weight(1f)
		)
	}
}