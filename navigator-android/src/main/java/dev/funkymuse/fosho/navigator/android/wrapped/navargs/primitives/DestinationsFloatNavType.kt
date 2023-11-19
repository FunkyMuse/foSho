package dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_NULL

/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
object DestinationsFloatNavType : DestinationsNavType<Float?>() {

    override fun put(bundle: Bundle, key: String, value: Float?) {
        if (value == null) {
            bundle.putByte(key, 0)
        } else {
            bundle.putFloat(key, value)
        }
    }

    override fun get(bundle: Bundle, key: String): Float? =
        @Suppress("DEPRECATION")
        (floatValue(bundle[key]))

    override fun parseValue(value: String): Float? =
        if (value == DECODED_NULL) {
            null
        } else {
            FloatType.parseValue(value)
        }

    override fun serializeValue(value: Float?): String =
        value?.toString() ?: ENCODED_NULL

    override fun get(savedStateHandle: SavedStateHandle, key: String): Float? =
        floatValue(savedStateHandle[key])

    private fun floatValue(valueForKey: Any?): Float? =
        if (valueForKey is Float) {
            valueForKey
        } else {
            null
        }
}
