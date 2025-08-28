package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
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
import com.sam.qrforge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(
	modifier: Modifier = Modifier,
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
						Text(stringResource(R.string.action_scan))
					}
				},
				state = rememberTooltipState()
			) {
				IconButton(onClick = {}) {
					Icon(
						painter = painterResource(R.drawable.ic_scan),
						contentDescription = stringResource(R.string.action_scan)
					)
				}
			}
		},
	)
}