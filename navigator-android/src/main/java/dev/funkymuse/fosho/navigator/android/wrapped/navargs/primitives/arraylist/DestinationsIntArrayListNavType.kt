package dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist

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
object DestinationsIntArrayListNavType : DestinationsNavType<ArrayList<Int>?>() {

    override fun put(bundle: Bundle, key: String, value: ArrayList<Int>?) {
        bundle.putIntegerArrayList(key, value)
    }

    override fun get(bundle: Bundle, key: String): ArrayList<Int>? =
        bundle.getIntegerArrayList(key)

    override fun parseValue(value: String): ArrayList<Int>? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> arrayListOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)
                if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }.mapTo(ArrayList()) { IntType.parseValue(it) }
            }
        }

    override fun serializeValue(value: ArrayList<Int>?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): ArrayList<Int>? =
        savedStateHandle[key]
}
