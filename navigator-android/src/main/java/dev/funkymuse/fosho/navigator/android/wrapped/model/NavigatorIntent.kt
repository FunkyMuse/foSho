package dev.funkymuse.fosho.navigator.android.wrapped.model

import androidx.compose.runtime.Stable
import androidx.navigation.NavOptionsBuilder

@Stable
sealed interface NavigatorIntent {
    data object NavigateUp : NavigatorIntent
    data object PopCurrentBackStack : NavigatorIntent
    data class PopBackStack(
        val route: String,
        val inclusive: Boolean,
        val saveState: Boolean = false,
    ) : NavigatorIntent

    class Directions(
        val destination: String,
        val builder: NavOptionsBuilder.() -> Unit = {},
    ) : NavigatorIntent {
        override fun toString(): String = "destination=$destination"
    }

    data class NavigateTopLevel(val route: String) : NavigatorIntent
}

val String.asDirectionsSingleTop: NavigatorIntent.Directions
    get() = NavigatorIntent.Directions(this) {
        launchSingleTop = true
    }
val String.asDirections: NavigatorIntent.Directions get() = NavigatorIntent.Directions(this)
