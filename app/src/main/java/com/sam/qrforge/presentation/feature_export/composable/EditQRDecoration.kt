package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQRDecoration(
	onDecorationChange: (QRDecorationOption) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(16.dp),
	enterTransition: EnterTransition = fadeIn() + slideInVertically(),
	exitTransition: ExitTransition = fadeOut() + slideOutVertically(),
) {

	val showBackgroundColorPicker by remember(decoration) {
		derivedStateOf {
			when (decoration) {
				is QRDecorationOption.QRDecorationOptionBasic -> true
				is QRDecorationOption.QRDecorationOptionColorLayer -> false
				is QRDecorationOption.QRDecorationOptionMinimal -> decoration.showBackground
			}
		}
	}

	val showFrameOption by remember(decoration) {
		derivedStateOf { decoration is QRDecorationOption.QRDecorationOptionBasic }
	}

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
				text = stringResource(R.string.action_edit),
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary,
			)
			// list of options
			QREditBlockRoundness(
				roundness = decoration.roundness,
				onRoundnessChange = { round ->
					val modified = decoration.copyProperties(roundness = round)
					onDecorationChange(modified)
				},
			)
			QREditBlockBitsSize(
				bitsSizeMultiplier = decoration.bitsSizeMultiplier,
				onBitsMultiplierChange = { bitsSize ->
					val modified = decoration.copyProperties(bitsSizeMultiplier = bitsSize)
					onDecorationChange(modified)
				}
			)
			QREditBlockContentMargin(
				contentMargin = decoration.contentMargin,
				onMarginChange = { margin ->
					val modified = decoration.copyProperties(contentMargin = margin)
					onDecorationChange(modified)
				}
			)
			HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
			AnimatedVisibility(
				visible = showBackgroundColorPicker,
				enter = enterTransition,
				exit = exitTransition
			) {
				QREditBlockSelectColor(
					title = stringResource(R.string.qr_edit_property_background_color_title),
					selectedColor = decoration.backGroundColor ?: Color.Transparent,
					onSelectColor = {
						val modified = decoration.copyBackgroundColor(it)
						onDecorationChange(modified)
					},
				)
			}
			QREditBlockSelectColor(
				title = stringResource(R.string.qr_edit_property_bits_color_title),
				selectedColor = decoration.bitsColor ?: MaterialTheme.colorScheme.onBackground,
				onSelectColor = {
					val modified = decoration.copyBitsColor(it)
					onDecorationChange(modified)
				},
			)
			QREditBlockSelectColor(
				title = stringResource(R.string.qr_edit_property_finders_color_title),
				selectedColor = decoration.findersColor ?: MaterialTheme.colorScheme.onBackground,
				onSelectColor = {
					val modified = decoration.copyFinderColor(it)
					onDecorationChange(modified)
				},
			)
			HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
			QREditBlockFinderShape(
				isShapeDiamond = decoration.isDiamond,
				onChangeShape = { isDiamond ->
					val modified = decoration.copyProperties(isDiamond = isDiamond)
					onDecorationChange(modified)
				},
			)
			AnimatedVisibility(
				visible = showFrameOption,
				enter = enterTransition,
				exit = exitTransition
			) {
				(decoration as? QRDecorationOption.QRDecorationOptionBasic)?.let {
					QREditBlockShowFrame(
						showFrame = decoration.showFrame,
						onShowFrameChange = { show ->
							onDecorationChange(decoration.copy(showFrame = show))
						},
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun EditQRDecorationsPreview() = QRForgeTheme {
	EditQRDecoration(onDecorationChange = {})
}