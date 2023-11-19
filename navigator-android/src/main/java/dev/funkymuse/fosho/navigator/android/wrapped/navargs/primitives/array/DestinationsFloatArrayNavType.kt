package dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.encodedComma

/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
object DestinationsFloatArrayNavType : DestinationsNavType<FloatArray?>() {

    override fun put(bundle: Bundle, key: String, value: FloatArray?) {
        bundle.putFloatArray(key, value)
    }

    override fun get(bundle: Bundle, key: String): FloatArray? =
        bundle.getFloatArray(key)

    override fun parseValue(value: String): FloatArray? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> floatArrayOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)
                val splits = if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }

                FloatArray(splits.size) { FloatType.parseValue(splits[it]) }
            }
        }

    override fun serializeValue(value: FloatArray?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): FloatArray? =
        savedStateHandle[key]
}
