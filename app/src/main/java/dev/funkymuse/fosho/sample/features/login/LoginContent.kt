package dev.funkymuse.fosho.sample.features.login

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import dev.funkymuse.fosho.navigator.android.LoginDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object LoginContent : LoginDestination {
    @Composable
    override fun AnimatedContentScope.Content() {
        LoginScreen(Navigator::navigateUp)
    }

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)
        get() = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up)
        }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)
        get() =  {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down)
        }
}