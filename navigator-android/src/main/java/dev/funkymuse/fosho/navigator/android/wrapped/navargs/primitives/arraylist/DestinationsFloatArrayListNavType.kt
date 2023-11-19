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
object DestinationsFloatArrayListNavType : DestinationsNavType<ArrayList<Float>?>() {

    override fun put(bundle: Bundle, key: String, value: ArrayList<Float>?) {
        bundle.putFloatArray(key, value?.let { list -> FloatArray(list.size) { list[it] } })
    }

    override fun get(bundle: Bundle, key: String): ArrayList<Float>? =
        bundle.getFloatArray(key).toArrayList()

    override fun parseValue(value: String): ArrayList<Float>? =
        when (value) {
            DECODED_NULL -> null
            "[]" -> arrayListOf()
            else -> {
                val contentValue = value.subSequence(1, value.length - 1)
                if (contentValue.contains(encodedComma)) {
                    contentValue.split(encodedComma)
                } else {
                    contentValue.split(",")
                }.mapTo(ArrayList()) { FloatType.parseValue(it) }
            }
        }

    override fun serializeValue(value: ArrayList<Float>?): String {
        value ?: return ENCODED_NULL
        return "[${value.joinToString(",") { it.toString() }}]"
    }

    override fun get(savedStateHandle: SavedStateHandle, key: String): ArrayList<Float>? =
        savedStateHandle.get<FloatArray?>(key).toArrayList()

    private fun FloatArray?.toArrayList(): ArrayList<Float>? =
        this?.let { ArrayList(it.toList()) }
}
