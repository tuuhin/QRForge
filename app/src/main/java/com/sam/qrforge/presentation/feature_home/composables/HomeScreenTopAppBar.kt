package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(
	modifier: Modifier = Modifier,
	onSettings: () -> Unit = {},
	onFilter: () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null
) {
	MediumTopAppBar(
		title = { Text(stringResource(R.string.app_name)) },
		scrollBehavior = scrollBehavior,
		modifier = modifier,
		actions = {
			TooltipBox(
				positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
				tooltip = {
					PlainTooltip {
						Text(text = stringResource(R.string.filter_options))
					}
				},
				state = rememberTooltipState()
			) {
				Button(
					onClick = onFilter,
					shape = MaterialTheme.shapes.extraLarge,
					contentPadding = PaddingValues(),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_filter),
						contentDescription = "Filter Option"
					)
				}
			}
			Spacer(modifier = Modifier.width(8.dp))
			TooltipBox(
				positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
				tooltip = {
					PlainTooltip {
						Text(text = stringResource(R.string.settings_options))
					}
				},
				state = rememberTooltipState()
			) {
				OutlinedButton(
					onClick = onSettings,
					shape = MaterialTheme.shapes.extraLarge,
					contentPadding = PaddingValues(),
				) {
					Icon(
						painter = painterResource(R.drawable.ic_settings),
						contentDescription = "Settings"
					)
				}
			}
			Spacer(modifier = Modifier.width(8.dp))
		},
	)
}