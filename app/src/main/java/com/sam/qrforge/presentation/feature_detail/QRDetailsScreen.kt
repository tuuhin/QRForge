package com.sam.qrforge.presentation.feature_detail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_detail.composables.QRDetailsScreenContent
import com.sam.qrforge.presentation.feature_detail.composables.QRDetailsTopAppBar
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
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onNavigateToHome: () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val layoutDirection = LocalLayoutDirection.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			QRDetailsTopAppBar(
				qrModel = state.qrModel,
				scrollBehavior = scrollBehavior,
				navigation = navigation,
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
		QRDetailsScreenContent(
			state = state,
			onNavigateBackToHome = onNavigateToHome,
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding() + dimensionResource(R.dimen.sc_padding),
				bottom = scPadding.calculateBottomPadding() + dimensionResource(R.dimen.sc_padding),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
		)
	}
}

private class QRDetailsScreenStatePreviewParam :
	CollectionPreviewParameterProvider<QRDetailsScreenState>(
		listOf(
			QRDetailsScreenState(
				qrModel = PreviewFakes.FAKE_QR_MODEL,
				generatedModel = PreviewFakes.FAKE_GENERATED_UI_MODEL,
				isLoading = false
			),
			QRDetailsScreenState(
				qrModel = null,
				generatedModel = null,
				isLoading = false
			),
		)
	)

@PreviewLightDark
@Composable
private fun QRDetailsScreenPreview(
	@PreviewParameter(QRDetailsScreenStatePreviewParam::class)
	state: QRDetailsScreenState
) = QRForgeTheme {
	QRDetailsScreen(
		qrId = -1L,
		state = state,
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}