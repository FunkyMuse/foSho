package dev.funkymuse.fosho.navigator.android.navigator.impl



import androidx.navigation.NavOptionsBuilder
import dev.funkymuse.fosho.navigator.android.navigator.AndroidNavigator
import dev.funkymuse.fosho.navigator.android.navigator.NavigatorDirections
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object Navigator : AndroidNavigator, NavigatorDirections {

    private val navigationEvents = Channel<NavigatorIntent>(Channel.BUFFERED)
    override val directions = navigationEvents.receiveAsFlow()

    override fun popCurrentBackStack() {
        navigationEvents.trySend(NavigatorIntent.PopCurrentBackStack)
    }

    override fun navigateUp() {
        navigationEvents.trySend(NavigatorIntent.NavigateUp)
    }

    override fun popBackStack(destination: String, inclusive: Boolean, saveState: Boolean) {
        navigationEvents.trySend(NavigatorIntent.PopBackStack(destination, inclusive, saveState))
    }

    override fun navigateSingleTop(destination: String, builder: NavOptionsBuilder.() -> Unit) {
        navigationEvents.trySend(NavigatorIntent.Directions(destination, builder))
    }

    override fun navigateTopLevel(destination: String) {
        navigationEvents.trySend(NavigatorIntent.NavigateTopLevel(destination))
    }

    override fun navigate(navigatorIntent: NavigatorIntent) {
        navigationEvents.trySend(navigatorIntent)
    }

    override fun navigate(destination: String) {
        navigationEvents.trySend(NavigatorIntent.Directions(destination))
    }

    override fun navigate(destination: String, builder: NavOptionsBuilder.() -> Unit) {
        navigationEvents.trySend(NavigatorIntent.Directions(destination, builder))
    }
}
