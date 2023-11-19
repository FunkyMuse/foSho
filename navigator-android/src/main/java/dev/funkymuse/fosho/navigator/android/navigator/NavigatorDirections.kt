package dev.funkymuse.fosho.navigator.android.navigator

import androidx.compose.runtime.Immutable
import dev.funkymuse.fosho.navigator.android.wrapped.model.NavigatorIntent
import kotlinx.coroutines.flow.Flow

@Immutable
interface NavigatorDirections {

    val directions: Flow<NavigatorIntent>
}