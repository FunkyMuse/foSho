package dev.funkymuse.fosho.navigator.android

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentContract
import dev.funkymuse.fosho.navigator.codegen.contract.Screen

interface AndroidScreen : NavigationDestination, Screen {
    val argumentsList: List<ArgumentContract>
        get() = emptyList()

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = null

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = null

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = enterTransition

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = exitTransition

    @Composable
    fun AnimatedContentScope.Content()
}
