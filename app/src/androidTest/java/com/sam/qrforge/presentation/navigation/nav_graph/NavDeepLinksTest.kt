package com.sam.qrforge.presentation.navigation.nav_graph

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class NavDeepLinksTest : KoinTest {

    private val context by inject<Context>()

    @get:Rule
    val intentsRule = IntentsRule()

    @Test
    fun resolve_backStack_with_create_new_uri_returns_home_followed_by_create_route() {
        val intent = viewIntent(NavDeepLinks.CREATE_NEW_QR_DEEP_LINK)

        val backStack = NavDeepLinks.resolveBackStack(intent)
        assertThat(backStack.toList())
            .containsExactly(NavRoutes.HomeRoute, NavRoutes.CreateRoute)
    }

    @Test
    fun resolve_backstack_with_scan_new_uri_returns_home_followed_by_scan_route() {
        val intent = viewIntent(NavDeepLinks.SCAN_QR_DEEP_LINK)

        val backStack = NavDeepLinks.resolveBackStack(intent)

        assertThat(backStack.toList())
            .containsExactly(NavRoutes.HomeRoute, NavRoutes.ScanRoute)
    }

    @Test
    fun implicit_create_deep_link_resolves_to_main_activity() {
        val intent = viewIntent(NavDeepLinks.CREATE_NEW_QR_DEEP_LINK)
        assertManifestResolves(intent)
    }

    @Test
    fun implicit_scan_deep_link_resolves_to_main_activity() {
        val intent = viewIntent(NavDeepLinks.SCAN_QR_DEEP_LINK)
        assertManifestResolves(intent)
    }

    @Test
    fun espresso_intents_observes_create_link_resolved_to_main_activity() {
        assertIntentResolution(NavDeepLinks.CREATE_NEW_QR_DEEP_LINK)
    }

    @Test
    fun espresso_intents_observes_scan_link_resolved_to_main_activity() {
        assertIntentResolution(NavDeepLinks.SCAN_QR_DEEP_LINK)
    }

    @Test
    fun resolve_backstack_with_unknown_uri_returns_only_homeRoute() {
        val intent = viewIntent("app://com.sam.qrforge/unknown")

        val backStack = NavDeepLinks.resolveBackStack(intent)
        assertThat(backStack.toList()).containsExactly(NavRoutes.HomeRoute)
    }

    @Test
    fun resolve_backStack_with_null_intent_returns_only_home_route() {
        val backStack = NavDeepLinks.resolveBackStack(null)
        assertThat(backStack.toList()).containsExactly(NavRoutes.HomeRoute)
    }

    @Test
    fun resolve_backStack_with_intent_without_data_returns_only_home_Route() {
        val intent = Intent(Intent.ACTION_MAIN)

        val backStack = NavDeepLinks.resolveBackStack(intent)
        assertThat(backStack.toList()).containsExactly(NavRoutes.HomeRoute)
    }

    private fun viewIntent(uri: String): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(uri))

    private fun assertManifestResolves(intent: Intent) {
        assertThat(intent.resolveActivity(context.packageManager)?.className)
            .isEqualTo("com.sam.qrforge.MainActivity")
    }

    private fun assertIntentResolution(uri: String) {
        intending(hasAction(Intent.ACTION_VIEW))
            .respondWith(ActivityResult(Activity.RESULT_OK, null))

        context.startActivity(viewIntent(uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        intended(hasAction(Intent.ACTION_VIEW))
    }

}
