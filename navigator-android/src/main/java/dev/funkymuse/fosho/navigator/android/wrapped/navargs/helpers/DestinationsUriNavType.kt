package dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_DEFAULT_VALUE_STRING_PREFIX
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_EMPTY_STRING
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DECODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.ENCODED_NULL
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.encodeForRoute

object DestinationsUriNavType : DestinationsNavType<Uri?>() {

    override fun put(bundle: Bundle, key: String, value: Uri?) {
        StringType.put(bundle, key, value.toString())
    }

    override fun get(bundle: Bundle, key: String): Uri? =
        StringType[bundle, key]?.toUri()

    override fun parseValue(value: String): Uri? {
        if (value.startsWith(DECODED_DEFAULT_VALUE_STRING_PREFIX)) {
            return value.removePrefix(DECODED_DEFAULT_VALUE_STRING_PREFIX).toUri()
        }

        return when (value) {
            DECODED_NULL -> null
            DECODED_EMPTY_STRING -> Uri.EMPTY
            else -> value.toUri()
        }
    }

    override fun serializeValue(value: Uri?): String =
        when (value) {
            null -> ENCODED_NULL
            Uri.EMPTY -> DECODED_EMPTY_STRING
            else -> encodeForRoute(value.toString())
        }

    override fun get(savedStateHandle: SavedStateHandle, key: String): Uri? =
        savedStateHandle.get<String?>(key)?.toUri()
}
