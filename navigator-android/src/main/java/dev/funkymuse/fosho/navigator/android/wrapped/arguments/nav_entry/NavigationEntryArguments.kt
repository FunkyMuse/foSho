package dev.funkymuse.fosho.navigator.android.wrapped.arguments.nav_entry

import android.os.Bundle
import androidx.compose.runtime.Immutable
import androidx.navigation.NavBackStackEntry

@Immutable
interface NavigationEntryArguments {
    val currentNavBackStackEntry: NavBackStackEntry
    val arguments: Bundle? get() = currentNavBackStackEntry.arguments
}
