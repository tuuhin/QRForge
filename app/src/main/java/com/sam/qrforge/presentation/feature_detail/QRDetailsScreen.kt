package com.sam.qrforge.presentation.feature_detail

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_detail.composables.MissingQRDetailsContent
import com.sam.qrforge.presentation.feature_detail.composables.QRDetailsScreenContent
import com.sam.qrforge.presentation.feature_detail.composables.QRDetailsTopAppBar
import com.sam.qrforge.presentation.feature_detail.composables.QREditActionButton
import com.sam.qrforge.presentation.feature_detail.state.ContentLoadState
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenEvents
import com.sam.qrforge.presentation.feature_detail.state.QRDetailsScreenState
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun QRDetailsScreen(
	qrId: Long,
	state: QRDetailsScreenState,
	onEvent: (QRDetailsScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
) {
	val snackBarHostState = LocalSnackBarState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	val loadState by remember(state) {
		derivedStateOf {
			when {
				state.isLoading -> ContentLoadState.IS_LOADING
				!state.isLoading && state.qrModel != null -> ContentLoadState.DATA_READY
				else -> ContentLoadState.DATA_ABSENT
			}
		}
	}

	Scaffold(
		topBar = {
			QRDetailsTopAppBar(
				qrModel = state.qrModel,
				scrollBehavior = scrollBehavior,
				onToggleFavourite = { onEvent(QRDetailsScreenEvents.ToggleIsFavourite(it)) },
				navigation = navigation,
			)
		},
		floatingActionButton = {
			QREditActionButton(
				showButton = state.qrModel != null,
				onEdit = {},
			)
		},
		snackbarHost = {
			SnackbarHost(snackBarHostState) { data ->
				Snackbar(
					snackbarData = data,
					shape = MaterialTheme.shapes.large,
					containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
					contentColor = MaterialTheme.colorScheme.onBackground,
					actionContentColor = MaterialTheme.colorScheme.primary,
				)
			}
		},
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(SharedTransitionKeys.sharedBoundsToItemDetail(qrId))
	) { scPadding ->
		Crossfade(
			targetState = loadState,
			animationSpec = tween(200, easing = FastOutSlowInEasing),
			modifier = Modifier
				.fillMaxSize()
				.padding(scPadding)
				.padding(all = dimensionResource(R.dimen.sc_padding))
		) { screenState ->
			when (screenState) {
				ContentLoadState.IS_LOADING -> LoadingContent()
				ContentLoadState.DATA_ABSENT -> MissingQRDetailsContent(
					onBackToHome = onNavigateToHome,
					modifier = Modifier.fillMaxSize()
				)

				ContentLoadState.DATA_READY -> state.qrModel?.let { qrModel ->
					QRDetailsScreenContent(
						savedContent = qrModel,
						generatedModel = state.generatedModel,
						onShare = { onEvent(QRDetailsScreenEvents.OnShareQR) },
						onExport = {},
						modifier = Modifier.fillMaxSize()
					)
				}
			}
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
		Spacer(modifier = Modifier.height(20.dp))
		Text(
			text = stringResource(R.string.content_loading_text),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.onSurface,
		)
	}
}

@PreviewLightDark
@Composable
private fun QRDetailsScreenPreview() = QRForgeTheme {
	QRDetailsScreen(
		qrId = -1L,
		state = QRDetailsScreenState(
			qrModel = PreviewFakes.FAKE_QR_MODEL,
			generatedModel = PreviewFakes.FAKE_GENERATED_UI_MODEL,
			isLoading = false
		),
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}