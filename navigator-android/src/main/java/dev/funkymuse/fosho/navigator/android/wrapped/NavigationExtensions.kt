package dev.funkymuse.fosho.navigator.android.wrapped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.funkymuse.fosho.navigator.android.navigator.AndroidNavigator
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent


@Composable
fun NavHostController.rememberPreviousEntry(): NavBackStackEntry? = remember {
    previousBackStackEntry
}

val NavHostController.currentEntry
    get() = currentBackStackEntry
        ?: throw IllegalStateException("$currentDestination with $currentBackStackEntry is null, this is unexpected and shouldn't happen")


const val HIDE_BOTTOM_NAV_ARG = "hideBottomNav"

/**
 * Add this argument to the list of [NavigationDestination.arguments] to control
 * the visibility (HIDE) of the bottom nav bar
 */
val hideBottomNamedArgument: NamedNavArgument
    get() = navArgument(HIDE_BOTTOM_NAV_ARG) {
        type = NavType.BoolType
        defaultValue = true
    }

/**
 * Add this argument to the list of [NavigationDestination.arguments] to control
 * the visibility (SHOW) of the bottom nav bar
 */
val showBottomNamedArgument: NamedNavArgument
    get() = navArgument(HIDE_BOTTOM_NAV_ARG) {
        type = NavType.BoolType
        defaultValue = false //hide = false means show
    }

/**
 * If not presented in the [NavigationDestination.arguments], default value is true
 */
val NavBackStackEntry?.hideBottomNavigation
    get() = this?.arguments?.getBoolean(
        HIDE_BOTTOM_NAV_ARG,
        true,
    ) ?: true


fun AndroidNavigator.onDestinationIntent(intent: NavigatorIntent) {
    when (intent) {
        is NavigatorIntent.Directions -> navigateSingleTop(intent.destination, intent.builder)
        is NavigatorIntent.NavigateUp -> navigateUp()
        is NavigatorIntent.PopCurrentBackStack -> popCurrentBackStack()
        is NavigatorIntent.PopBackStack -> popBackStack(
            intent.route,
            intent.inclusive,
            intent.saveState,
        )

        is NavigatorIntent.NavigateTopLevel -> navigateTopLevel(intent.route)
    }
}