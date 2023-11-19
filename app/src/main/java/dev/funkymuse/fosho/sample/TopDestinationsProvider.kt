package dev.funkymuse.fosho.sample

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.funkymuse.fosho.navigator.android.FavoritesNavigationGraph
import dev.funkymuse.fosho.navigator.android.HomeNavigationGraph
import dev.funkymuse.fosho.navigator.android.SettingsNavigationGraph
import dev.funkymuse.fosho.sample.bottom_navigation.BottomNavigationEntry
import dev.funkymuse.fosho.sample.features.favorites.FavoritesContent
import dev.funkymuse.fosho.sample.features.home.HomeContent
import dev.funkymuse.fosho.sample.features.settings.SettingsContent
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@ActivityRetainedScoped
class TopDestinationsProvider @Inject constructor() {
    fun getStartingDestination(): String = HomeNavigationGraph.route

    val bottomNavigationEntries = listOf(
        BottomNavigationEntry(
            destination = HomeContent,
            text = R.string.home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = HomeNavigationGraph.route
        ),

        BottomNavigationEntry(
            destination = FavoritesContent,
            text = R.string.favorites,
            selectedIcon = Icons.Default.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            route = FavoritesNavigationGraph.route
        ),
        BottomNavigationEntry(
            destination = SettingsContent,
            text = R.string.settings,
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = SettingsNavigationGraph.route
        ),
    ).toImmutableList()

}
