package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.composables.painter
import com.sam.qrforge.presentation.common.composables.string
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.utils.PLAIN_DATE
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@Composable
fun QRModelCard(
	model: SavedAndGeneratedQRModel,
	onDeleteItem: () -> Unit,
	modifier: Modifier = Modifier,
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
		state = state,
		enableDismissFromEndToStart = false,
		backgroundContent = {
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
		},
		modifier = modifier,
	) {
		Card(
			shape = shape,
			colors = CardDefaults.cardColors(containerColor, contentColor),
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(all = dimensionResource(R.dimen.card_internal_padding)),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				AnimatedQRContent(uiModel = model.uiModel)
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
					QRDataCardTypeAndDateInfo(
						qrModel = model.qrModel,
						modifier = Modifier.fillMaxWidth()
					)
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
	size: DpSize = DpSize(100.dp, 100.dp),
) {
	AnimatedVisibility(
		visible = uiModel != null,
		enter = slideInHorizontally() + expandIn(),
		exit = slideOutHorizontally() + shrinkOut(),
		modifier = modifier,
	) {
		uiModel?.let { generatedModel ->
			QRTemplateBasic(
				model = generatedModel,
				backgroundColor = Color.Transparent,
				contentMargin = contentMargin,
				roundness = .5f,
				modifier = Modifier.size(size)
			)
		}
	}
}

@Composable
private fun QRDataCardTypeAndDateInfo(
	qrModel: SavedQRModel,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Surface(
			shape = MaterialTheme.shapes.medium,
			color = MaterialTheme.colorScheme.tertiaryContainer
		) {
			Row(
				modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					painter = qrModel.format.painter,
					contentDescription = "Icon ${qrModel.format.name}",
					modifier = Modifier.size(20.dp)
				)
				Text(
					text = qrModel.format.string,
					style = MaterialTheme.typography.labelMedium
				)
			}
		}
		Text(
			text = qrModel.modifiedAt.format(LocalDateTime.Formats.PLAIN_DATE),
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
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
		onDeleteItem = {}
	)
}