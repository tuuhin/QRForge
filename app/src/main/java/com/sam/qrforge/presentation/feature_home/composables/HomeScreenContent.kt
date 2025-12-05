package com.sam.qrforge.presentation.feature_home.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sam.qrforge.R
import com.sam.qrforge.domain.models.SavedQRModel
import com.sam.qrforge.presentation.feature_home.state.FilterQRListState
import com.sam.qrforge.presentation.feature_home.state.HomeScreenEvents
import com.sam.qrforge.presentation.feature_home.state.ListContentState
import com.sam.qrforge.presentation.feature_home.state.SavedAndGeneratedQRModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HomeScreenContent(
	generatedQR: ImmutableList<SavedAndGeneratedQRModel>,
	onEvent: (HomeScreenEvents) -> Unit,
	modifier: Modifier = Modifier,
	onSelectItem: (SavedQRModel) -> Unit = {},
	filterState: FilterQRListState = FilterQRListState(),
	isContentReady: Boolean = true,
	contentPadding: PaddingValues = PaddingValues(0.dp)
) {

	val isListFullyEmpty by remember(filterState, generatedQR) {
		derivedStateOf { filterState.selectedType == null && generatedQR.isEmpty() }
	}

	val contentState by remember(isContentReady, generatedQR) {
		derivedStateOf {
			when {
				!isContentReady -> ListContentState.IS_LOADING
				generatedQR.isEmpty() -> ListContentState.EMPTY
				else -> ListContentState.DATA
			}
		}
	}

	Column(
		modifier = modifier.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(6.dp)
	) {
		QRDataTypePickerRow(
			selectedType = filterState.selectedType,
			onSelectType = { type ->
				val newState = filterState.copy(selectedType = type)
				onEvent(HomeScreenEvents.OnListFilterChange(newState))
			},
			modifier = Modifier.fillMaxWidth()
		)
		HorizontalDivider()
		Crossfade(
			targetState = contentState,
			animationSpec = tween(durationMillis = 200, delayMillis = 90, easing = EaseInOut),
			modifier = Modifier.weight(1f)
		) { state ->
			when (state) {
				ListContentState.IS_LOADING -> LoadingContent()
				ListContentState.EMPTY -> EmptyContent(isListFullyEmpty = isListFullyEmpty)
				ListContentState.DATA -> QRModelList(
					generatedQR = generatedQR,
					onSelectItem = onSelectItem,
					onGenerateQR = { onEvent(HomeScreenEvents.OnGenerateQR(it)) },
					onDeleteItem = { onEvent(HomeScreenEvents.OnDeleteItem(it)) },
					modifier = Modifier.fillMaxSize(),
				)
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
		Text(
			text = stringResource(R.string.content_loading_text),
			style = MaterialTheme.typography.titleLarge
		)
	}
}

@Composable
private fun EmptyContent(
	modifier: Modifier = Modifier,
	isListFullyEmpty: Boolean = false
) {
	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize(),
	) {
		Image(
			painter = painterResource(R.drawable.ic_qr_2),
			contentDescription = "QR Logo",
			modifier = Modifier.size(192.dp),
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
		)
		Text(
			text = if (isListFullyEmpty) stringResource(R.string.no_qr_saved)
			else stringResource(R.string.no_qr_saved_typed),
			style = MaterialTheme.typography.bodyLarge,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.primary,
		)
	}
}