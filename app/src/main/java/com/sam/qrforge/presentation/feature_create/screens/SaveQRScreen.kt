package com.sam.qrforge.presentation.feature_create.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.utils.LocalSnackBarState
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenEvents
import com.sam.qrforge.presentation.feature_create.state.SaveQRScreenState
import com.sam.qrforge.ui.theme.QRForgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveQRScreen(
	state: SaveQRScreenState,
	onEvent: (SaveQRScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {
	val snackBarHostState = LocalSnackBarState.current

	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
	val focusRequester = remember { FocusRequester() }

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = "Save QR ") },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(
				onClick = { onEvent(SaveQRScreenEvents.OnSave) },
				shape = MaterialTheme.shapes.large,
				icon = {
					Icon(
						painter = painterResource(R.drawable.ic_save),
						contentDescription = "Save"
					)
				},
				text = { Text(text = "Save") },
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
	) { scPadding ->

		Column(
			modifier = modifier
				.padding(scPadding)
				.padding(all = dimensionResource(R.dimen.sc_padding)),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Text(
				text = "Only the content is being saved qr editing is on the go and not saved ",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			OutlinedTextField(
				value = state.title,
				onValueChange = { value -> onEvent(SaveQRScreenEvents.OnSaveQRTitleChange(value)) },
				label = { Text(text = "Title") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Next
				),
				keyboardActions = KeyboardActions(
					onNext = { focusRequester.requestFocus(FocusDirection.Down) },
				),
				placeholder = { Text(text = "My device QR") },
				shape = MaterialTheme.shapes.medium,
				modifier = Modifier.fillMaxWidth(),
			)
			OutlinedTextField(
				value = state.desc,
				onValueChange = { value -> onEvent(SaveQRScreenEvents.OnSaveQRDescChange(value)) },
				label = { Text(text = "Description") },
				placeholder = { Text(text = "Optional description") },
				keyboardOptions = KeyboardOptions(
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Next
				),
				shape = MaterialTheme.shapes.medium,
				minLines = 2,
				singleLine = false,
				modifier = Modifier
					.focusRequester(focusRequester)
					.fillMaxWidth(),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun SaveQRScreenPreview() = QRForgeTheme {
	SaveQRScreen(
		state = SaveQRScreenState(), onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.ArrowBack,
				contentDescription = "Back Arrow"
			)
		},
	)
}