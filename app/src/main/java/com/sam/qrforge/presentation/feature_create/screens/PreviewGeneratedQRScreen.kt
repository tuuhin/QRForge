package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.qr.QRContentModel
import com.sam.qrforge.domain.models.qr.QRPlainTextModel
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.common.utils.SharedTransitionKeys
import com.sam.qrforge.presentation.common.utils.sharedBoundsWrapper
import com.sam.qrforge.presentation.feature_create.CreateNewQREvents
import com.sam.qrforge.presentation.feature_create.composables.GeneratedQRContainerWithActions
import com.sam.qrforge.presentation.feature_create.composables.QRActualDataCard
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalSharedTransitionApi::class
)
@Composable
fun PreviewGeneratedQRScreen(
	content: QRContentModel,
	modifier: Modifier = Modifier,
	generated: GeneratedQRUIModel? = null,
	onEvent: (CreateNewQREvents) -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = "Preview QR Code") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					TextButton(onClick = {}) {
						Text("Save")
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.sharedBoundsWrapper(SharedTransitionKeys.CREATE_QR_SCREEN_TO_GENERATE_SCREEN)
	) { scPadding ->
		Column(
			modifier = Modifier
				.padding(scPadding)
				.padding(horizontal = dimensionResource(R.dimen.sc_padding))
				.fillMaxSize(),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			GeneratedQRContainerWithActions(generated = generated)
			Spacer(modifier = Modifier.height(16.dp))
			QRActualDataCard(content = content)
		}
	}
}

@PreviewLightDark
@Composable
private fun PreviewGeneratedQRScreenPreview() = QRForgeTheme {
	PreviewGeneratedQRScreen(
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