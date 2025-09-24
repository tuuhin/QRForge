package com.sam.qrforge.presentation.feature_export.composable.edit_blocks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.models.copyBackgroundColor
import com.sam.qrforge.presentation.common.models.copyBitsColor
import com.sam.qrforge.presentation.common.models.copyFinderColor
import com.sam.qrforge.presentation.feature_export.composable.ColorOptionSelector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QREditBlockColorSelector(
	title: String,
	onSelectColor: (Color) -> Unit,
	modifier: Modifier = Modifier,
	selectedColor: Color? = null,
	titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
	titleColor: Color = MaterialTheme.colorScheme.onSurface,
	bodyStyle: TextStyle = MaterialTheme.typography.labelMedium,
	bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {

	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()

	var showSheet by remember { mutableStateOf(false) }

	if (showSheet) {
		ModalBottomSheet(
			onDismissRequest = { showSheet = false },
			sheetState = sheetState,
		) {
			Column(
				modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.bottom_sheet_content_padding)),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ColorOptionSelector(
					onColorChange = { color -> onSelectColor(color) },
					selected = selectedColor,
					contentPadding = PaddingValues.Zero
				)
				Button(
					onClick = {
						scope.launch { sheetState.hide() }
							.invokeOnCompletion { showSheet = false }
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.tertiary,
						contentColor = MaterialTheme.colorScheme.onTertiary
					),
					shape = MaterialTheme.shapes.extraLarge,
					modifier = Modifier.align(Alignment.End)
				) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = "Checked"
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(text = stringResource(R.string.action_done))
				}
			}
		}
	}

	val background = if (selectedColor == Color.Transparent)
		MaterialTheme.colorScheme.surfaceVariant
	else selectedColor

	Box(
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.clickable(
				onClick = {
					scope.launch { sheetState.show() }
						.invokeOnCompletion { showSheet = true }
				},
			)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			modifier = Modifier.padding(all = 4.dp),
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Text(
					text = title,
					style = titleStyle,
					color = titleColor,
				)
				Text(
					text = stringResource(R.string.qr_edit_property_select_color_text),
					style = bodyStyle,
					color = bodyColor
				)
			}
			Box(
				modifier = Modifier
					.size(32.dp)
					.then(
						if (background != null) Modifier.background(
							background,
							MaterialTheme.shapes.medium
						)
						else Modifier
					),
			)
		}
	}
}

@Composable
fun QREditDecorationColorOptions(
	decoration: QRDecorationOption,
	onDecorationChange: (QRDecorationOption) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(12.dp),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {

	val hasBackground by remember(decoration) {
		derivedStateOf { decoration.canHaveBackgroundOption }
	}

	val showFrameColorOption by remember(decoration) {
		derivedStateOf { decoration is QRDecorationOption.QRDecorationOptionBasic && decoration.showFrame }
	}

	val isLayerOption by remember(decoration) {
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
			AnimatedVisibility(visible = hasBackground) {
				QREditBlockColorSelector(
					title = stringResource(R.string.qr_edit_property_background_color_title),
					selectedColor = decoration.backGroundColor
						?: MaterialTheme.colorScheme.background,
					onSelectColor = {
						val modified = decoration.copyBackgroundColor(it)
						onDecorationChange(modified)
					},
				)
			}
			AnimatedVisibility(visible = showFrameColorOption) {
				if (decoration !is QRDecorationOption.QRDecorationOptionBasic) return@AnimatedVisibility

				QREditBlockColorSelector(
					title = stringResource(R.string.qr_edit_property_basic_frame_color_title),
					selectedColor = decoration.frameColor ?: MaterialTheme.colorScheme.onBackground,
					onSelectColor = {
						val modified = decoration.copy(frameColor = it)
						onDecorationChange(modified)
					},
				)
			}
			AnimatedVisibility(visible = !isLayerOption) {
				QREditBlockColorSelector(
					title = stringResource(R.string.qr_edit_property_bits_color_title),
					selectedColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
					onSelectColor = {
						val modified = decoration.copyBitsColor(it)
						onDecorationChange(modified)
					},
				)
			}
			AnimatedVisibility(visible = !isLayerOption) {
				QREditBlockColorSelector(
					title = stringResource(R.string.qr_edit_property_finders_color_title),
					selectedColor = decoration.findersColor
						?: MaterialTheme.colorScheme.onBackground,
					onSelectColor = {
						val modified = decoration.copyFinderColor(it)
						onDecorationChange(modified)
					},
				)
			}
		}
	}
}