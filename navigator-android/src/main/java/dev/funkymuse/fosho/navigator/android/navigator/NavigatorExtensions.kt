package dev.funkymuse.fosho.navigator.android.navigator

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent
import kotlinx.coroutines.flow.collectLatest


@Composable
fun NavHostControllerEvents(
    navigator: NavigatorDirections,
    navController: NavHostController,
) {
    LaunchedEffect(navController) {
        navigator.directions.collectLatest { navigatorEvent ->
            when (navigatorEvent) {
                is NavigatorIntent.NavigateUp -> navController.navigateUp()
                is NavigatorIntent.Directions -> navController.navigate(
                    navigatorEvent.destination,
                    navigatorEvent.builder,
                )

                NavigatorIntent.PopCurrentBackStack -> navController.popBackStack()
                is NavigatorIntent.PopBackStack -> navController.popBackStack(
                    navigatorEvent.route,
                    navigatorEvent.inclusive,
                    navigatorEvent.saveState,
                )

                is NavigatorIntent.NavigateTopLevel -> {
                    val topLevelNavOptions = navOptions {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                    navController.navigate(navigatorEvent.route, topLevelNavOptions)
                }
            }
        }
    }
}

@Composable
fun ListenForIntentRedirections(
    navController: NavHostController,
    onListenForRedirections: (Intent?, NavBackStackEntry?) -> Unit
) {
    val activity = (LocalContext.current.getActivity() as ComponentActivity)
    DisposableEffect(navController) {
        val listener = Consumer<Intent> {
            onListenForRedirections(it, navController.currentBackStackEntry)
        }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}

internal fun Context.getActivity(): Activity {
    if (this is Activity) return this
    return if (this is ContextWrapper) baseContext.getActivity() else getActivity()
}
