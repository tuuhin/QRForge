package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.QRContentTypeChip
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.utils.PLAIN_DATE
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.common.utils.sharedElementWrapper
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun QRModelCard(
	model: SavedAndGeneratedQRModel,
	onDeleteItem: () -> Unit,
	onSelectItem: () -> Unit,
	modifier: Modifier = Modifier,
	onGenerateQR: () -> Unit = {},
	shape: Shape = MaterialTheme.shapes.large,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	val state = rememberSwipeToDismissBoxState(
		confirmValueChange = { value ->
			val isStartToEnd = value == SwipeToDismissBoxValue.StartToEnd
			if (isStartToEnd) onDeleteItem()
			isStartToEnd
		},
	)

	SwipeToDismissBox(
		state = state, enableDismissFromEndToStart = false, backgroundContent = {
			when (state.dismissDirection) {
				SwipeToDismissBoxValue.StartToEnd -> {
					SwipeToDeleteContent(
						progress = { state.progress },
						shape = shape,
						modifier = Modifier.fillMaxSize()
					)
				}

				else -> {}
			}
		}, modifier = modifier
	) {
		Card(
			onClick = onSelectItem,
			shape = shape,
			colors = CardDefaults.cardColors(containerColor, contentColor),
			modifier = Modifier.onFirstVisible(minDurationMs = 10, callback = onGenerateQR)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(all = dimensionResource(R.dimen.card_internal_padding)),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				AnimatedQRContent(
					uiModel = model.uiModel, modifier = Modifier.sharedElementWrapper(
						SharedTransitionKeys.sharedElementQRCodeItemToDetail(model.qrModel.id)
					)
				)
				Column(
					modifier = Modifier.weight(1f),
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
							Text(
								text = model.qrModel.title,
								style = MaterialTheme.typography.titleMedium,
								color = MaterialTheme.colorScheme.primary,
								fontWeight = FontWeight.SemiBold,
								modifier = Modifier.sharedBoundsWrapper(
									SharedTransitionKeys.sharedBoundsTitleToDetails(model.qrModel.id)
								)
							)
							model.qrModel.desc?.let { desc ->
								Text(
									text = desc,
									style = MaterialTheme.typography.bodyMedium,
									maxLines = 2,
									overflow = TextOverflow.Ellipsis,
								)
							}
						}
						if (model.qrModel.isFav) {
							Icon(
								painter = painterResource(R.drawable.ic_heart),
								contentDescription = "Favourites",
								tint = MaterialTheme.colorScheme.primary,
							)
						}
					}
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						QRContentTypeChip(
							type = model.qrModel.format,
							modifier = Modifier.sharedElementWrapper(
								SharedTransitionKeys.sharedElementContentTypeCard(model.qrModel.id)
							)
						)
						Text(
							text = model.qrModel.modifiedAt.format(LocalDateTime.Formats.PLAIN_DATE),
							style = MaterialTheme.typography.labelMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
						)
					}
				}
			}
		}
	}
}


@Composable
private fun AnimatedQRContent(
	modifier: Modifier = Modifier,
	uiModel: GeneratedQRUIModel? = null,
	contentMargin: Dp = 0.dp,
	contentBackground: Color = MaterialTheme.colorScheme.surfaceContainer,
	size: DpSize = DpSize(100.dp, 100.dp),
) {
	Box(
		modifier = modifier.size(size),
		contentAlignment = Alignment.Center
	) {
		Crossfade(
			targetState = uiModel != null,
			animationSpec = tween(easing = EaseInOut, durationMillis = 200),
		) { isReady ->
			if (isReady && uiModel != null) QRTemplateBasic(
				model = uiModel,
				decoration = QRDecorationOption.QRDecorationOptionBasic(
					roundness = .5f,
					contentMargin = contentMargin,
					backGroundColor = contentBackground
				),
				modifier = Modifier.fillMaxSize(),
			)
			else Box(
				modifier = Modifier
					.matchParentSize()
					.background(
						color = MaterialTheme.colorScheme.surfaceContainerHigh,
						shape = MaterialTheme.shapes.large
					),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun SelectableQRModelCardPreview() = QRForgeTheme {
	QRModelCard(
		model = SavedAndGeneratedQRModel(
			qrModel = PreviewFakes.FAKE_QR_MODEL,
			uiModel = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL
		),
		onDeleteItem = {},
		onSelectItem = {},
	)
}