package dev.funkymuse.fosho.sample.features.favorites

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import dev.funkymuse.fosho.navigator.android.FavoriteDestination
import dev.funkymuse.fosho.navigator.android.GreenBottomSheetDestination
import dev.funkymuse.fosho.navigator.android.YellowDialogDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object FavoritesContent : FavoriteDestination{
    @Composable
    override fun AnimatedContentScope.Content() {
        val favoritesViewModel = hiltViewModel<FavoritesViewModel>()
        val testItems by favoritesViewModel.testFlow.collectAsStateWithLifecycle()
        Log.d("FavoritesScreen", testItems.toString())
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Favorites")
            Button(onClick = {
                Navigator.navigateSingleTop(GreenBottomSheetDestination.openGreenBottomSheet())
            }) {
                Text(text = "Bottom sheet")
            }
            Button(onClick = {
                Navigator.navigateSingleTop(YellowDialogDestination.openYellowDialog())
            }) {
                Text(text = "Dialog")
            }
        }
    }

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
        get() = {
            slideInHorizontally(tween()) {
                -it
            }
        }

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
        get() = {
            slideOutVertically(tween()) { it }
        }
}