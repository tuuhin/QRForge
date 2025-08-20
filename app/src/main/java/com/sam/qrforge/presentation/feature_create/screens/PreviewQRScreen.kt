package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.models.QRDecorationOption
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_create.CreateNewQREvents
import com.sam.qrforge.presentation.feature_create.composables.PreviewQRCodeTopAppBar
import com.sam.qrforge.presentation.feature_create.composables.PreviewQRScreenContent
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun PreviewQRScreen(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	templateDecoration: QRDecorationOption = QRDecorationOption.QRDecorationOptionBasic(),
	onEvent: (CreateNewQREvents) -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val layoutDirection = LocalLayoutDirection.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			PreviewQRCodeTopAppBar(
				onSaveQR = {},
				navigation = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(SharedTransitionKeys.CREATE_QR_SCREEN_TO_GENERATE_SCREEN)
	) { scPadding ->
		PreviewQRScreenContent(
			content = content,
			generated = generated,
			templateDecoration = templateDecoration,
			onDecorationChange = { onEvent(CreateNewQREvents.OnDecorationChange(it)) },
			onTemplateChange = { onEvent(CreateNewQREvents.OnQRTemplateChange(it)) },
			contentPadding = PaddingValues(
				top = scPadding.calculateTopPadding(),
				bottom = scPadding.calculateBottomPadding(),
				start = scPadding.calculateStartPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding),
				end = scPadding.calculateEndPadding(layoutDirection) + dimensionResource(R.dimen.sc_padding)
			),
			modifier = Modifier.fillMaxSize()
		)
	}
}

@PreviewLightDark
@Composable
private fun PreviewQRScreenPreview() = QRForgeTheme {
	PreviewQRScreen(
		generated = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
		content = QRPlainTextModel("Hello"),
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}