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
object DestinationsLongNavType : DestinationsNavType<Long?>() {

    override fun put(bundle: Bundle, key: String, value: Long?) {
        if (value == null) {
            bundle.putByte(key, 0)
        } else {
            bundle.putLong(key, value)
        }
    }

    override fun get(bundle: Bundle, key: String): Long? =
        @Suppress("DEPRECATION")
        (longValue(bundle[key]))

    override fun parseValue(value: String): Long? =
        if (value == DECODED_NULL) {
            null
        } else {
            LongType.parseValue(value)
        }

    override fun serializeValue(value: Long?): String =
        value?.toString() ?: ENCODED_NULL

    override fun get(savedStateHandle: SavedStateHandle, key: String): Long? =
        longValue(savedStateHandle[key])

    private fun longValue(valueForKey: Any?): Long? =
        if (valueForKey is Long) {
            valueForKey
        } else {
            null
        }
}
