package dev.funkymuse.fosho.navigator.android.wrapped.navargs

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType

/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
abstract class DestinationsNavType<T : Any?> : NavType<T>(true) {

    abstract fun serializeValue(value: T): String?

    abstract fun get(savedStateHandle: SavedStateHandle, key: String): T

    fun safeGet(bundle: Bundle?, key: String): T? =
        bundle?.let { get(it, key) }

}
