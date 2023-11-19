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
object DestinationsBooleanNavType : DestinationsNavType<Boolean?>() {

    override fun put(bundle: Bundle, key: String, value: Boolean?) {
        if (value == null) {
            bundle.putByte(key, 0)
        } else {
            bundle.putBoolean(key, value)
        }
    }

    override fun get(bundle: Bundle, key: String): Boolean? =
        @Suppress("DEPRECATION")
        (booleanValue(bundle[key]))

    override fun parseValue(value: String): Boolean? =
        if (value == DECODED_NULL) {
            null
        } else {
            BoolType.parseValue(value)
        }

    override fun serializeValue(value: Boolean?): String =
        value?.toString() ?: ENCODED_NULL

    override fun get(savedStateHandle: SavedStateHandle, key: String): Boolean? =
        booleanValue(savedStateHandle.get<Any?>(key))

    private fun booleanValue(valueForKey: Any?): Boolean? =
        if (valueForKey is Boolean) {
            valueForKey
        } else {
            null
        }
}
