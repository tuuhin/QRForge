package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.painter
import com.sam.qrforge.presentation.common.composables.string
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.common.utils.PLAIN_DATE
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_home.state.SelectableQRModel
import com.sam.qrforge.ui.theme.QRForgeTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

@Composable
fun SelectableQRModelCard(
	selectableModel: SelectableQRModel,
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.medium,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	Card(
		shape = shape,
		colors = CardDefaults.cardColors(containerColor, contentColor),
		modifier = modifier,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(all = dimensionResource(R.dimen.card_internal_padding)),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			selectableModel.generated?.let { generatedModel ->
				QRTemplateBasic(
					model = generatedModel,
					backgroundColor = Color.Transparent,
					contentMargin = 0.dp,
					roundness = .5f,
					modifier = Modifier.size(108.dp)
				)
			}
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
						Text(
							text = selectableModel.qrModel.title,
							style = MaterialTheme.typography.titleLarge,
							color = MaterialTheme.colorScheme.primary,
							fontWeight = FontWeight.SemiBold,
						)
						selectableModel.qrModel.desc?.let { desc ->
							Text(
								text = desc,
								style = MaterialTheme.typography.bodyMedium,
								maxLines = 2,
								overflow = TextOverflow.Ellipsis,
							)
						}
					}
					AnimatedVisibility(
						visible = selectableModel.qrModel.isFav,
						enter = slideInHorizontally() + expandHorizontally(),
						exit = slideOutHorizontally() + shrinkHorizontally(),
						modifier = Modifier.align(Alignment.CenterVertically)
					) {
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
					SuggestionChip(
						onClick = {},
						enabled = false,
						border = null,
						shape = MaterialTheme.shapes.medium,
						label = {
							Text(
								text = selectableModel.qrModel.format.string,
								style = MaterialTheme.typography.labelMedium
							)
						},
						icon = {
							Icon(
								painter = selectableModel.qrModel.format.painter,
								contentDescription = "Icon ${selectableModel.qrModel.format.name}",
								modifier = Modifier.size(20.dp)
							)
						},
						colors = SuggestionChipDefaults.suggestionChipColors(
							disabledLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
							disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
							disabledIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
						)
					)
					Text(
						text = selectableModel.qrModel.modifiedAt.format(LocalDateTime.Formats.PLAIN_DATE),
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun SelectableQRModelCardPreview() = QRForgeTheme {
	SelectableQRModelCard(
		selectableModel = SelectableQRModel(
			qrModel = PreviewFakes.FAKE_QR_MODEL,
			isSelected = true,
			generated = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL
		),
	)
}