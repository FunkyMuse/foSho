package dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_DEFAULT_VALUE_STRING_PREFIX
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_EMPTY_STRING
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.encodeForRoute
import java.io.File

object DestinationsFileNavType : DestinationsNavType<File?>() {

    override fun put(bundle: Bundle, key: String, value: File?) {
        StringType.put(bundle, key, value?.path.toString())
    }

    override fun get(bundle: Bundle, key: String): File? =
        StringType[bundle, key]?.let(::File)

    override fun parseValue(value: String): File? {
        if (value.startsWith(DECODED_DEFAULT_VALUE_STRING_PREFIX)) {
            return File(value.removePrefix(DECODED_DEFAULT_VALUE_STRING_PREFIX))
        }

        return when (value) {
            DECODED_NULL -> null
            DECODED_EMPTY_STRING -> null
            else -> File(value)
        }
    }

    override fun serializeValue(value: File?): String =
        when (value) {
            null -> ENCODED_NULL
            else -> encodeForRoute(value.toString())
        }

    override fun get(savedStateHandle: SavedStateHandle, key: String): File? =
        savedStateHandle.get<String?>(key)?.let(::File)
}
