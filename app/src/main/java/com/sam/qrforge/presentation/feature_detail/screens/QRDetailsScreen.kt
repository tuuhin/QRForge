package com.sam.qrforge.presentation.feature_detail.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.sam.qrforge.presentation.common.composables.AppCustomSnackBar
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.common.utils.sharedTransitionSkipChildSize
import com.sam.qrforge.presentation.feature_detail.composables.ConfirmDeleteDialog
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
	onNavigateToEdit: () -> Unit = {},
	onNavigateToExport: () -> Unit = {},
) {
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
	val scrollState = rememberScrollState()

	val hasScrolled by remember(scrollState) {
		derivedStateOf { scrollState.value > 0 }
	}

	val loadState by remember(state) {
		derivedStateOf {
			when {
				state.isLoading -> ContentLoadState.IS_LOADING
				!state.isLoading && state.qrModel != null -> ContentLoadState.DATA_READY
				else -> ContentLoadState.DATA_ABSENT
			}
		}
	}

	ConfirmDeleteDialog(
		showDialog = state.showDeleteDialog,
		canDelete = state.deleteEnabled,
		onDismiss = { onEvent(QRDetailsScreenEvents.ToggleDeleteDialog) },
		onConfirmDelete = { onEvent(QRDetailsScreenEvents.DeleteCurrentQR) },
	)

	Scaffold(
		topBar = {
			QRDetailsTopAppBar(
				qrModel = state.qrModel,
				onToggleFavourite = { onEvent(QRDetailsScreenEvents.ToggleIsFavourite(it)) },
				onDeleteItem = { onEvent(QRDetailsScreenEvents.ToggleDeleteDialog) },
				navigation = navigation,
				scrollBehavior = scrollBehavior,
				modifier = Modifier.sharedTransitionSkipChildSize()
			)
		},
		floatingActionButton = {
			QREditActionButton(
				showButton = state.qrModel != null,
				onEdit = onNavigateToEdit,
				isExpanded = !hasScrolled,
				shape = MaterialTheme.shapes.large,
			)
		},
		snackbarHost = { AppCustomSnackBar() },
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(
				key = SharedTransitionKeys.sharedBoundsToItemDetail(qrId),
				resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
				placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
			)
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
						onConnectToWifi = { onEvent(QRDetailsScreenEvents.ActionConnectToWifi) },
						onShare = { bitmap -> onEvent(QRDetailsScreenEvents.OnShareQR(bitmap)) },
						onExport = onNavigateToExport,
						scrollState = scrollState,
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
			qrModel = PreviewFakes.FAKE_QR_MODEL_2,
			generatedModel = PreviewFakes.FAKE_GENERATED_UI_MODEL_4,
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