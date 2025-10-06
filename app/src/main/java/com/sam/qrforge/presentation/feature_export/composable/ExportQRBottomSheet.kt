package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.enums.ExportDimensions
import com.sam.qrforge.domain.enums.ImageMimeTypes
import com.sam.qrforge.ui.theme.QRForgeTheme
import com.sam.qrforge.ui.theme.displayFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportQRBottomSheet(
	showSheet: Boolean,
	onCancelExport: () -> Unit,
	onBeginExport: () -> Unit,
	modifier: Modifier = Modifier,
	selectedExportType: ImageMimeTypes = ImageMimeTypes.PNG,
	selectedDimension: ExportDimensions = ExportDimensions.Medium,
	onDimensionChange: (ExportDimensions) -> Unit = {},
	onExportTypeChange: (ImageMimeTypes) -> Unit = {},
	isExportRunning: Boolean = false,
	sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
	containerColor: Color = BottomSheetDefaults.ContainerColor,
	tonalElevation: Dp = BottomSheetDefaults.Elevation,
) {
	if (!showSheet) return

	ModalBottomSheet(
		onDismissRequest = onCancelExport,
		sheetState = sheetState,
		modifier = modifier,
		containerColor = containerColor,
		tonalElevation = tonalElevation,
	) {
		ExportQRBottomSheetContent(
			selectedExportType = selectedExportType,
			onImageFormatChange = onExportTypeChange,
			selectedDimension = selectedDimension,
			onDimensionChange = onDimensionChange,
			onExport = onBeginExport,
			onCancelExport = onCancelExport,
			isExportRunning = isExportRunning,
			contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.bottom_sheet_content_padding))
		)
	}
}

@Composable
private fun ExportQRBottomSheetContent(
	onCancelExport: () -> Unit,
	onExport: () -> Unit,
	modifier: Modifier = Modifier,
	onImageFormatChange: (ImageMimeTypes) -> Unit = {},
	onDimensionChange: (ExportDimensions) -> Unit = {},
	isExportRunning: Boolean = false,
	selectedExportType: ImageMimeTypes = ImageMimeTypes.PNG,
	selectedDimension: ExportDimensions = ExportDimensions.Medium,
	contentPadding: PaddingValues = PaddingValues(12.dp)
) {
	Column(
		modifier = modifier
			.windowInsetsPadding(WindowInsets.navigationBars)
			.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Crossfade(
			targetState = isExportRunning,
			animationSpec = tween(durationMillis = 400, delayMillis = 100, easing = EaseInOut)
		) { isExporting ->
			if (isExporting) {
				Text(
					text = stringResource(R.string.export_qr_bottom_sheet_title_running),
					style = MaterialTheme.typography.headlineMedium,
					fontFamily = displayFontFamily,
					color = MaterialTheme.colorScheme.onSurface
				)
			} else Text(
				text = stringResource(R.string.export_qr_bottom_sheet_title),
				style = MaterialTheme.typography.headlineMedium,
				fontFamily = displayFontFamily,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
		Text(
			text = stringResource(R.string.export_qr_bottom_sheet_text),
			style = MaterialTheme.typography.bodyLarge,
			color = if (isExportRunning) MaterialTheme.colorScheme.onSurfaceVariant
			else MaterialTheme.colorScheme.onSurface
		)
		ExportDimensionPicker(
			onDimensionChange = onDimensionChange,
			selected = selectedDimension,
			enabled = !isExportRunning,
			contentPadding = PaddingValues(12.dp),
			containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
		)
		ExportMimeTypePicker(
			onFormatChange = onImageFormatChange,
			selectedFormat = selectedExportType,
			enabled = !isExportRunning,
			contentPadding = PaddingValues(12.dp),
			containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
		)
		AnimatedVisibility(
			visible = isExportRunning,
			enter = slideInHorizontally() + fadeIn(),
			exit = slideOutHorizontally() + fadeOut(),
		) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth(),
				strokeCap = StrokeCap.Round
			)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			Button(
				onClick = onCancelExport,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.tertiary,
					contentColor = MaterialTheme.colorScheme.onTertiary
				)
			) {
				Text(
					text = stringResource(R.string.dialog_action_cancel),
					style = MaterialTheme.typography.titleMedium
				)
			}
			Button(
				onClick = onExport,
				enabled = !isExportRunning
			) {
				Text(
					text = stringResource(R.string.action_export),
					style = MaterialTheme.typography.titleMedium
				)
			}
		}
		// An extra space
		Spacer(modifier = Modifier.height(2.dp))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ExportQRBottomSheetPreview() = QRForgeTheme {
	Surface(
		color = BottomSheetDefaults.ContainerColor,
		shape = BottomSheetDefaults.ExpandedShape
	) {
		ExportQRBottomSheetContent(
			onExport = {},
			onCancelExport = {},
			contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.bottom_sheet_content_padding))
		)
	}
}