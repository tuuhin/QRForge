package com.sam.qrforge.presentation.navigation.util

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

interface NavDestinationBuilder {

    fun EntryProviderScope<NavKey>.navEntry(backStack: NavBackStack<NavKey>)
}
