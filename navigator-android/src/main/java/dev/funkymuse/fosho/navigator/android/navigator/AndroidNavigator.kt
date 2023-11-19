package dev.funkymuse.fosho.navigator.android.navigator

import androidx.compose.runtime.Immutable
import androidx.navigation.NavOptionsBuilder
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent

@Immutable
interface AndroidNavigator {

    fun navigateUp()
    fun navigateSingleTop(
        destination: String,
        builder: NavOptionsBuilder.() -> Unit = { launchSingleTop = true },
    )

    fun navigate(
        destination: String,
    )

    fun navigateTopLevel(
        destination: String,
    )

    fun navigate(
        destination: String,
        builder: NavOptionsBuilder.() -> Unit,
    )

    fun popBackStack(
        destination: String,
        inclusive: Boolean,
        saveState: Boolean = false,
    )

    fun popCurrentBackStack()

    fun navigate(navigatorIntent: NavigatorIntent)
}
