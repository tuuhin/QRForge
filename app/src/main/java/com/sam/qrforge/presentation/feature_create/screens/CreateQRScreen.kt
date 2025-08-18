package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.feature_create.CreateNewQREvents
import com.sam.qrforge.presentation.feature_create.composables.QRContentContainer
import com.sam.qrforge.presentation.feature_create.composables.QRDataTypeSelector
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQRScreen(
	modifier: Modifier = Modifier,
	content: QRContentModel,
	onEvent: (CreateNewQREvents) -> Unit = {},
	isContentValid: Boolean = true,
	navigation: @Composable () -> Unit = {},
	onGenerateQR: () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text("Create QR Code") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
				actions = {
					AnimatedVisibility(
						visible = isContentValid,
						enter = slideInVertically() + expandIn(),
						exit = slideOutVertically() + shrinkOut()
					) {
						TextButton(
							onClick = onGenerateQR,
							enabled = isContentValid
						) {
							Text(text = "Generate")
						}
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier
			.nestedScroll(scrollBehavior.nestedScrollConnection)
			.imePadding()
	) { scPadding ->
		LazyColumn(
			contentPadding = scPadding,
			modifier = Modifier
				.padding(dimensionResource(R.dimen.sc_padding))
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			item {
				QRDataTypeSelector(
					selectedType = content.type,
					onSelectType = { onEvent(CreateNewQREvents.OnQRDataTypeChange(it)) },
					modifier = Modifier.fillMaxWidth()
				)
			}
			item {
				QRContentContainer(
					selectedType = content.type,
					content = content,
					onUseCurrentLocation = { onEvent(CreateNewQREvents.CheckLastKnownLocation) },
					onReadContactsDetails = { onEvent(CreateNewQREvents.CheckContactsDetails(it)) },
					onContentChange = { content ->
						onEvent(CreateNewQREvents.OnUpdateQRContent(content))
					},
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun CreateQRScreenPreview() = QRForgeTheme {
	CreateQRScreen(
		content = QRPlainTextModel("Something"),
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Arrow"
			)

		},
	)
}