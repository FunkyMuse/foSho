package dev.funkymuse.fosho.sample.features.settings

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import dev.funkymuse.fosho.navigator.android.SettingsDestination
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object SettingsContent : SettingsDestination {
    @Composable
    override fun AnimatedContentScope.Content() {
        //val settingsViewModel = hiltViewModel<SettingsViewModel>()
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Settings")
        }
    }

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = {
            slideInHorizontally(tween())
        }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() =  {
            slideOutVertically(tween()) { -it }
        }
}