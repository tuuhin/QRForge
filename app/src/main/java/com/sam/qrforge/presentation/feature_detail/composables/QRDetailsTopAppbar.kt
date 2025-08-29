package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sam.qrforge.domain.models.SavedQRModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRDetailsTopAppBar(
	modifier: Modifier = Modifier,
	qrModel: SavedQRModel? = null,
	navigation: @Composable () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null,
) {
	MediumTopAppBar(
		title = {
			AnimatedVisibility(
				visible = qrModel != null,
				enter = slideInVertically(),
				exit = slideOutVertically()
			) {
				qrModel?.let { model ->
					Text(model.title)
				}
			}
		},
		actions = {
			AnimatedVisibility(
				visible = qrModel != null,
				enter = slideInVertically(),
				exit = slideOutVertically()
			) {
				TextButton(onClick = {}) {
					Text("Edit")
				}
			}
		},
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		modifier = modifier
	)
}