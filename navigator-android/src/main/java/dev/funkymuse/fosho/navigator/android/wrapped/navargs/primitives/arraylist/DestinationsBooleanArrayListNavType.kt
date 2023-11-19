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
object DestinationsBooleanArrayListNavType : DestinationsNavType<ArrayList<Boolean>?>() {

    override fun put(bundle: Bundle, key: String, value: ArrayList<Boolean>?) {
        bundle.putBooleanArray(key, value?.let { list -> BooleanArray(list.size) { list[it] } })
    }

    override fun get(bundle: Bundle, key: String): ArrayList<Boolean>? =
        bundle.getBooleanArray(key).toArrayList()

    override fun parseValue(value: String): ArrayList<Boolean>? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> arrayListOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)
                if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }.mapTo(ArrayList()) { BoolType.parseValue(it) }
            }
        }

    override fun serializeValue(value: ArrayList<Boolean>?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): ArrayList<Boolean>? =
        savedStateHandle.get<BooleanArray?>(key).toArrayList()

    private fun BooleanArray?.toArrayList(): ArrayList<Boolean>? =
        this?.let { ArrayList(it.toList()) }
}
