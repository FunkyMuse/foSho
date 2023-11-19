package dev.funkymuse.fosho.sample.bottom_navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.funkymuse.fosho.navigator.android.NavigationDestination

@Immutable
class BottomNavigationEntry(
    val destination: NavigationDestination,
    @StringRes val text: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)