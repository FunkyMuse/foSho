package dev.funkymuse.fosho.navigator.android.wrapped.navargs.serializable

import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavTypeSerializer
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.base64ToByteArray
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.toBase64Str
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
object DefaultSerializableNavTypeSerializer : DestinationsNavTypeSerializer<Serializable> {

    override fun toRouteString(value: Serializable): String =
        value.toBase64()

    override fun fromRouteString(routeStr: String): Serializable =
        base64ToSerializable(routeStr)

    private fun Serializable.toBase64(): String =
        ByteArrayOutputStream().use {
            val out = ObjectOutputStream(it)
            out.writeObject(this)
            out.flush()
            it.toByteArray().toBase64Str()
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T> base64ToSerializable(base64: String): T =
        ObjectInputStream(ByteArrayInputStream(base64.base64ToByteArray())).use { it.readObject() as T }
}
