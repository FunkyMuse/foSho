package dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_DEFAULT_VALUE_STRING_PREFIX
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_EMPTY_STRING
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_EMPTY_STRING
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.encodeForRoute
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.encodedComma
/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
object DestinationsStringNavType : DestinationsNavType<String?>() {

    override fun put(bundle: Bundle, key: String, value: String?) {
        StringType.put(bundle, key, value)
    }

    override fun get(bundle: Bundle, key: String): String? =
        StringType[bundle, key]

    override fun parseValue(value: String): String? {
        if (value.startsWith(DECODED_DEFAULT_VALUE_STRING_PREFIX)) {
            return value.removePrefix(DECODED_DEFAULT_VALUE_STRING_PREFIX)
        }

        return when (value) {
            DECODED_NULL -> null
            DECODED_EMPTY_STRING -> ""
            else -> value
        }
    }

    override fun serializeValue(value: String?): String =
        when {
            value == null -> ENCODED_NULL
            value.isEmpty() -> ENCODED_EMPTY_STRING
            else -> encodeForRoute(value)
        }

    override fun get(savedStateHandle: SavedStateHandle, key: String): String? =
        savedStateHandle[key]
}
