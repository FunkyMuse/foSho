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
object DestinationsLongArrayListNavType : DestinationsNavType<ArrayList<Long>?>() {

    override fun put(bundle: Bundle, key: String, value: ArrayList<Long>?) {
        bundle.putLongArray(key, value?.let { list -> LongArray(list.size) { list[it] } })
    }

    override fun get(bundle: Bundle, key: String): ArrayList<Long>? =
        bundle.getLongArray(key).toArrayList()

    override fun parseValue(value: String): ArrayList<Long>? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> arrayListOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)

                if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }.mapTo(ArrayList()) { LongType.parseValue(it) }
            }
        }

    override fun serializeValue(value: ArrayList<Long>?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): ArrayList<Long>? =
        savedStateHandle.get<LongArray?>(key).toArrayList()

    private fun LongArray?.toArrayList(): ArrayList<Long>? =
        this?.let { ArrayList(it.toList()) }
}
