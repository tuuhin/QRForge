package com.sam.qrforge.presentation.feature_detail.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.templates.QRTemplateBasic
import com.sam.qrforge.presentation.feature_detail.state.ContentLoadState
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenState

@Composable
fun QRDetailsScreenContent(
	state: QRDetailsScreenState,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	onNavigateBackToHome: () -> Unit = {},
) {
	val loadState by remember(state) {
		derivedStateOf {
			when {
				state.isLoading -> ContentLoadState.IS_LOADING
				!state.isLoading && state.qrModel != null -> ContentLoadState.DATA_READY
				else -> ContentLoadState.DATA_ABSENT
			}
		}
	}

	Crossfade(
		targetState = loadState,
		animationSpec = tween(200, easing = FastOutSlowInEasing),
		modifier = modifier.padding(contentPadding)
	) { contentState ->
		when (contentState) {
			ContentLoadState.IS_LOADING -> LoadingContent()
			ContentLoadState.DATA_ABSENT -> MissingQRDetailsContent(
				onBackToHome = onNavigateBackToHome,
				modifier = Modifier.fillMaxSize()
			)

			ContentLoadState.DATA_READY -> QRDetailsDataContent(
				generatedModel = state.generatedModel,
				savedContent = state.qrModel,
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize(),
	) {
		CircularProgressIndicator()
		Text(
			text = "Loading",
			style = MaterialTheme.typography.titleLarge
		)
	}
}

@Composable
private fun QRDetailsDataContent(
	modifier: Modifier = Modifier,
	generatedModel: GeneratedQRUIModel? = null,
	savedContent: SavedQRModel? = null,
) {
	val graphicsLayer = rememberGraphicsLayer()

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(4.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Surface(
			shape = MaterialTheme.shapes.extraLarge,
			color = MaterialTheme.colorScheme.surfaceContainer,
			modifier = Modifier.size(300.dp)
		) {
			if (generatedModel != null)
				QRTemplateBasic(
					model = generatedModel,
					backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
					graphicsLayer = { graphicsLayer },
					roundness = .5f,
					contentMargin = 0.dp,
					modifier = Modifier.fillMaxSize()
				)
		}
	}
}