package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun QRDetailsTopAppBar(
	modifier: Modifier = Modifier,
	qrModel: SavedQRModel? = null,
	navigation: @Composable () -> Unit = {},
	onToggleFavourite: (SavedQRModel) -> Unit = {},
	onDeleteItem: () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null,
) {
	MediumTopAppBar(
		title = {
			AnimatedVisibility(
				visible = qrModel != null,
				enter = slideInHorizontally(
					animationSpec = tween(durationMillis = 200, easing = EaseInOut)
				) + fadeIn(),
				exit = slideOutVertically(
					animationSpec = tween(durationMillis = 200, easing = EaseInOut)
				) + fadeOut(),
				modifier = Modifier.then(
					if (qrModel == null) Modifier
					else Modifier.sharedBoundsWrapper(
						SharedTransitionKeys.sharedBoundsTitleToDetails(qrModel.id),
					)
				)
			) {
				Text(
					text = qrModel?.title ?: "",
					maxLines = 1, overflow = TextOverflow.Ellipsis
				)
			}
		},
		actions = {
			qrModel?.let { model ->
				FilledTonalButton(
					onClick = { onToggleFavourite(model) },
					shape = MaterialTheme.shapes.extraLarge,
					colors = ButtonDefaults.filledTonalButtonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = MaterialTheme.colorScheme.onPrimaryContainer
					),
					contentPadding = PaddingValues(),
				) {
					Crossfade(
						targetState = model.isFav,
						animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
					) { isFav ->
						if (isFav) Icon(
							painter = painterResource(R.drawable.ic_heart),
							contentDescription = "Favourite"
						)
						else Icon(
							painter = painterResource(R.drawable.ic_heart_outlined),
							contentDescription = "Not favourite"
						)
					}
				}
				Spacer(modifier = Modifier.width(8.dp))
				OutlinedButton(
					onClick = onDeleteItem,
					shape = MaterialTheme.shapes.extraLarge,
					contentPadding = PaddingValues(),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_delete),
						contentDescription = "Delete Action"
					)
				}
			}
		},
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		modifier = modifier
	)
}