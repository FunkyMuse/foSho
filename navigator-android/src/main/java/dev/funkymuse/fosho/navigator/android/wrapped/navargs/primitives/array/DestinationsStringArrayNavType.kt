package dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
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
object DestinationsStringArrayNavType : DestinationsNavType<Array<String>?>() {

    override fun put(bundle: Bundle, key: String, value: Array<String>?) {
        bundle.putStringArray(key, value)
    }

    override fun get(bundle: Bundle, key: String): Array<String>? =
        bundle.getStringArray(key)

    override fun parseValue(value: String): Array<String>? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> arrayOf()
            else ->
                value
                    .subSequence(1, value.length - 1)
                    .split(encodedComma).let { splits ->
                        Array(splits.size) {
                            when (val split = splits[it]) {
                                DECODED_EMPTY_STRING -> ""
                                else -> split
                            }
                        }
                    }
        }

    override fun serializeValue(value: Array<String>?): String =
        when (value) {
            null -> ENCODED_NULL
            else -> encodeForRoute(
                "[" + value.joinToString(encodedComma) {
                    it.ifEmpty { ENCODED_EMPTY_STRING }
                } + "]",
            )
        }

    override fun get(savedStateHandle: SavedStateHandle, key: String): Array<String>? =
        savedStateHandle[key]
}
