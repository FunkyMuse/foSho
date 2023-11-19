package dev.funkymuse.fosho.navigator.android

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.bottomSheet
import dev.funkymuse.fosho.navigator.android.navigator.NavigatorDirections
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest

fun NavGraphBuilder.addGraphs(
    navigationGraphs: ImmutableList<AndroidGraph>,
    showAnimations: Boolean = true,
    onGraph: ((androidGraph: AndroidGraph) -> Unit)? = null
) {
    navigationGraphs.forEach { androidGraph ->
        onGraph?.invoke(androidGraph)
        navigation(
            startDestination = androidGraph.startingDestination.destination,
            route = androidGraph.route
        ) {
            androidGraph.bottomSheets.forEach { bottomSheet ->
                bottomSheet(
                    route = bottomSheet.destination,
                    arguments = bottomSheet.arguments,
                    deepLinks = bottomSheet.deepLinks
                ) {
                    bottomSheet.apply {
                        Content()
                    }
                }
            }
            androidGraph.dialogs.forEach { dialog ->
                dialog(
                    route = dialog.destination,
                    arguments = dialog.arguments,
                    deepLinks = dialog.deepLinks,
                    dialogProperties = dialog.dialogProperties
                ) {
                    dialog.Content()
                }
            }
            androidGraph.screens.forEach { screen ->
                if (showAnimations == false) {
                    composable(
                        route = screen.destination,
                        arguments = screen.arguments,
                        deepLinks = screen.deepLinks,
                    ) {
                        screen.apply {
                            Content()
                        }
                    }
                } else {
                    composable(
                        route = screen.destination,
                        arguments = screen.arguments,
                        deepLinks = screen.deepLinks,
                        enterTransition = screen.enterTransition,
                        exitTransition = screen.exitTransition,
                        popEnterTransition = screen.popEnterTransition,
                        popExitTransition = screen.popExitTransition
                    ) {
                        screen.apply {
                            Content()
                        }
                    }
                }
            }
        }
    }
}

fun NavHostController.navigateToTopLevelDestination(
    navigatorEvent: NavigatorIntent.NavigateTopLevel
) {
    val topLevelNavOptions = navOptions {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
    navigate(navigatorEvent.route, topLevelNavOptions)
}


@Composable
fun NavHostControllerEvents(
    navigator: NavigatorDirections,
    navController: NavHostController,
    onEvent: ((navigatorEvent: NavigatorIntent) -> Unit)? = null
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

                is NavigatorIntent.NavigateTopLevel -> navController.navigateToTopLevelDestination(
                    navigatorEvent
                )
            }
            onEvent?.invoke(navigatorEvent)
        }
    }
}

@Composable
fun OnNewIntentListener(key: Any, onIntent: (intent: Intent) -> Unit) {
    val context = LocalContext.current
    val currentOnIntent by rememberUpdatedState(newValue = onIntent)
    val activity = (context.getActivity() as ComponentActivity)
    DisposableEffect(key) {
        val listener = Consumer(currentOnIntent)
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}

private fun Context.getActivity(): Activity {
    if (this is Activity) return this
    return if (this is ContextWrapper) baseContext.getActivity() else getActivity()
}