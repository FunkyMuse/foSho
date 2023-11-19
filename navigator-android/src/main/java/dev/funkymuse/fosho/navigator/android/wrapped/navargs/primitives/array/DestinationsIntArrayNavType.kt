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
object DestinationsIntArrayNavType : DestinationsNavType<IntArray?>() {

    override fun put(bundle: Bundle, key: String, value: IntArray?) {
        bundle.putIntArray(key, value)
    }

    override fun get(bundle: Bundle, key: String): IntArray? =
        bundle.getIntArray(key)

    override fun parseValue(value: String): IntArray? {
        return when (value) {
            DECODED_NULL -> null
            "[]" -> intArrayOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)
                val splits = if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }

                IntArray(splits.size) { IntType.parseValue(splits[it]) }
            }
        }
    }

    override fun serializeValue(value: IntArray?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): IntArray? =
        savedStateHandle[key]
}
