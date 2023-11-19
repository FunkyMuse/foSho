package dev.funkymuse.fosho.sample.features.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.funkymuse.fosho.navigator.android.HomeDestination
import dev.funkymuse.fosho.navigator.android.LoginDestination
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.codegen.annotation.Content

@Content
internal object HomeContent : HomeDestination {
    @Composable
    override fun AnimatedContentScope.Content() {
        val homeViewModel = hiltViewModel<HomeViewModel>()
        HomeScreen(onClick = {
            Navigator.navigateSingleTop(LoginDestination.openLogin())
        })
    }
}