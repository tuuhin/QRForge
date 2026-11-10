package com.sam.qrforge.presentation.navigation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.sam.qrforge.R
import com.sam.qrforge.presentation.common.composables.AppIconVectors

@Composable
fun NavigationBackArrow(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {

    val defaultSpatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val defaultEffectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val fastSpatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val fastEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()

    AnimatedVisibility(
        visible = backStack.isNotEmpty(),
        enter = scaleIn(defaultEffectsSpec),
        exit = fadeOut(fastEffectsSpec) + scaleOut(defaultEffectsSpec),
        modifier = modifier,
    ) {
        FilledTonalIconButton(
            onClick = dropUnlessResumed { backStack.removeLastOrNull() },
            modifier = Modifier.offset(x = dimensionResource(R.dimen.sc_padding)),
        ) {
            Icon(
                imageVector = AppIconVectors.backArrow,
                contentDescription = "Back Arrow",
            )
        }
    }
}
