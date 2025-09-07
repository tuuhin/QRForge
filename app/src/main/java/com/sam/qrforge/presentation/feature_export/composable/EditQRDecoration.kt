package com.sam.qrforge.presentation.feature_export.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.ui.theme.QRForgeTheme

@Composable
fun EditQRDecoration(
	onDecorationChange: (QRDecorationOption) -> Unit,
	modifier: Modifier = Modifier,
	decoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentPadding: PaddingValues = PaddingValues(16.dp),
	enterTransition: EnterTransition = fadeIn() + slideInHorizontally(),
	exitTransition: ExitTransition = fadeOut() + slideOutHorizontally(),
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
				text = stringResource(R.string.action_edit),
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.secondary
			)
			// list of options
			QREditBlockRoundness(
				roundness = decoration.roundness,
				onRoundnessChange = { round ->
					val modified = when (decoration) {
						is QRDecorationOption.QRDecorationOptionBasic -> decoration.copy(roundness = round)
						is QRDecorationOption.QRDecorationOptionMinimal -> decoration.copy(roundness = round)
						is QRDecorationOption.QRDecorationOptionColorLayer ->
							decoration.copy(roundness = round)
					}
					onDecorationChange(modified)
				},
			)
			QREditBlockBitsSize(
				bitsSizeMultiplier = decoration.bitsSizeMultiplier,
				onBitsMultiplierChange = { bitsSize ->
					val modified = when (decoration) {
						is QRDecorationOption.QRDecorationOptionBasic ->
							decoration.copy(bitsSizeMultiplier = bitsSize)

						is QRDecorationOption.QRDecorationOptionMinimal ->
							decoration.copy(bitsSizeMultiplier = bitsSize)

						is QRDecorationOption.QRDecorationOptionColorLayer ->
							decoration.copy(bitsSizeMultiplier = bitsSize)
					}
					onDecorationChange(modified)
				}
			)
			QREditBlockContentMargin(
				contentMargin = decoration.contentMargin,
				onMarginChange = { margin ->
					val modified = when (decoration) {
						is QRDecorationOption.QRDecorationOptionBasic ->
							decoration.copy(contentMargin = margin)

						is QRDecorationOption.QRDecorationOptionMinimal ->
							decoration.copy(contentMargin = margin)

						is QRDecorationOption.QRDecorationOptionColorLayer ->
							decoration.copy(contentMargin = margin)
					}
					onDecorationChange(modified)
				}
			)
			QREditBlockFinderShape(
				isShapeDiamond = decoration.isDiamond,
				onChangeShape = { isDiamond ->
					val modified = when (decoration) {
						is QRDecorationOption.QRDecorationOptionBasic -> decoration.copy(isDiamond = isDiamond)
						is QRDecorationOption.QRDecorationOptionMinimal -> decoration.copy(isDiamond = isDiamond)
						is QRDecorationOption.QRDecorationOptionColorLayer ->
							decoration.copy(isDiamond = isDiamond)
					}
					onDecorationChange(modified)
				},
			)
			AnimatedVisibility(
				visible = decoration is QRDecorationOption.QRDecorationOptionBasic,
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