package dev.funkymuse.fosho.navigator.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import dev.funkymuse.fosho.navigator.android.wrapped.currentEntry

@Immutable
interface CallbackArguments {
    val navHostController: NavHostController
    fun consumeAllArgumentsAtReceivingDestination()
    private val currentBackStackEntrySavedStateHandle: SavedStateHandle?
        get() = navHostController.currentBackStackEntry?.savedStateHandle
    private val previousBackStackEntrySavedStateHandle: SavedStateHandle?
        get() = navHostController.previousBackStackEntry?.savedStateHandle

    fun <T> addArgumentToNavEntry(route: String, key: String, value: T?) {
        navHostController.getBackStackEntry(route).savedStateHandle[key] = value
    }

    fun consumeArgument(key: String) {
        currentBackStackEntrySavedStateHandle?.set(key, null)
    }

    fun <T> set(key: String, value: T?) {
        previousBackStackEntrySavedStateHandle?.set(key, value)
    }

    fun consumeArguments(vararg key: String) {
        key.forEach(::consumeArgument)
    }
}

@Composable
fun <T> CallbackArguments.OnSingleCallbackArgument(
    key: String,
    initialValue: T? = null,
    consumeWhen: (T?) -> Boolean = { it != null },
    onValue: (T?) -> Unit
) {
    val currentEntryHandle = navHostController.currentEntry.savedStateHandle
    val value by currentEntryHandle.getStateFlow(key, initialValue).collectAsState(initial = initialValue)
    val currentOnValue by rememberUpdatedState(newValue = onValue)
    LaunchedEffect(value) {
        currentOnValue(value)
        if (consumeWhen(value)) {
            consumeArgument(key)
        }
    }
}

@Composable
fun CallbackArguments.OnSingleBooleanCallbackArgument(
    key: String,
    initialValue: Boolean? = null,
    consumeWhen: (Boolean?) -> Boolean = { it != null },
    onValue: (Boolean?) -> Unit,
) = OnSingleCallbackArgument(key = key, initialValue = initialValue, onValue = onValue, consumeWhen = consumeWhen)

@Composable
fun CallbackArguments.OnSingleIntCallbackArgument(
    key: String,
    initialValue: Int? = null,
    consumeWhen: (Int?) -> Boolean = { it != null },
    onValue: (Int?) -> Unit,
) = OnSingleCallbackArgument(key = key, initialValue = initialValue, onValue = onValue, consumeWhen = consumeWhen)

@Composable
fun CallbackArguments.OnSingleStringCallbackArgument(
    key: String,
    initialValue: String? = null,
    consumeWhen: (String?) -> Boolean = { it != null },
    onValue: (String?) -> Unit,
) = OnSingleCallbackArgument(key = key, initialValue = initialValue, onValue = onValue, consumeWhen = consumeWhen)

@Composable
fun CallbackArguments.OnSingleDoubleCallbackArgument(
    key: String,
    initialValue: Double? = null,
    consumeWhen: (Double?) -> Boolean = { it != null },
    onValue: (Double?) -> Unit,
) = OnSingleCallbackArgument(key = key, initialValue = initialValue, onValue = onValue, consumeWhen = consumeWhen)
