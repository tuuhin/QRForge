package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.models.GeneratedQRUIModel
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.common.utils.PreviewFakes
import com.sam.qrforge.presentation.feature_create.CreateNewQREvents
import com.sam.qrforge.presentation.feature_create.composables.GeneratedQRContainer
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedQRScreen(
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
				title = { Text("Generated QR Code") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->
		Column(
			modifier = Modifier
				.padding(scPadding)
				.padding(dimensionResource(R.dimen.sc_padding))
				.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(4.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			GeneratedQRContainer(
				generated = generated,
				modifier = Modifier.size(300.dp)
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun GeneratedQRScreenPreview() = QRForgeTheme {
	GeneratedQRScreen(
		generated = PreviewFakes.FAKE_GENERATED_UI_MODEL_SMALL,
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}