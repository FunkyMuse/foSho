package dev.funkymuse.fosho.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.funkymuse.fosho.navigator.android.GraphFactory
import dev.funkymuse.fosho.navigator.android.LocalNavHostController
import dev.funkymuse.fosho.navigator.android.NavHostControllerEvents
import dev.funkymuse.fosho.navigator.android.addGraphs
import dev.funkymuse.fosho.navigator.android.navigator.NavigatorDirections
import dev.funkymuse.fosho.navigator.android.navigator.impl.Navigator
import dev.funkymuse.fosho.navigator.android.wrapped.hideBottomNavigation
import dev.funkymuse.fosho.sample.bottom_navigation.BottomNavigation
import dev.funkymuse.fosho.sample.bottom_navigation.BottomNavigationEntry
import dev.funkymuse.fosho.sample.theme.FoShoLibThemeSurface
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var topDestinationsProvider: TopDestinationsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)
        setContent {
            FoShoLibThemeSurface {
                AppScaffold(
                    startingDestination = topDestinationsProvider.getStartingDestination(),
                    showAnimations = true,
                    navigatorDirections = Navigator,
                    bottomNavigationEntries = topDestinationsProvider.bottomNavigationEntries
                )
            }
        }
    }
}


@Composable
private fun AppScaffold(
    startingDestination: String,
    showAnimations: Boolean,
    navigatorDirections: NavigatorDirections,
    bottomNavigationEntries: ImmutableList<BottomNavigationEntry>,
) {
    val bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator()
    val navController: NavHostController = rememberNavController(bottomSheetNavigator)

    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
        null
    )
    val showBottomNav: Boolean by remember {
        derivedStateOf { !navBackStackEntry.hideBottomNavigation }
    }
    val currentRoute by remember {
        derivedStateOf { navBackStackEntry?.destination?.route }
    }
    LaunchedEffect(currentRoute) {
        Log.d(
            "Current destination",
            "${navBackStackEntry?.destination} with arguments ${navBackStackEntry?.arguments}",
        )
    }

    CompositionLocalProvider(LocalNavHostController provides navController) {
        ModalBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = MaterialTheme.shapes.large.copy(
                topStart = CornerSize(16.dp),
                topEnd = CornerSize(16.dp)
            ),
            sheetElevation = 0.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startingDestination,
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        addGraphs(
                            navigationGraphs = GraphFactory.graphs,
                            showAnimations = showAnimations
                        )
                    }

                    BottomNavigation(
                        bottomEntries = bottomNavigationEntries,
                        modifier = Modifier.align(Alignment.BottomStart),
                        showBottomNav = showBottomNav,
                        navBackStackEntry = navBackStackEntry,
                        onTopLevelClick = Navigator::navigateTopLevel
                    )
                }
            }
        }
    }
    NavHostControllerEvents(
        navigator = navigatorDirections,
        navController = navController,
    )
}

