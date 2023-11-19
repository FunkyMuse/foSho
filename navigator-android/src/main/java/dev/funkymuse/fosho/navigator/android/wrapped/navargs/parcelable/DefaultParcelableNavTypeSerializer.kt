package dev.funkymuse.fosho.navigator.android.wrapped.navargs.parcelable

import android.os.BadParcelableException
import android.os.Parcel
import android.os.Parcelable
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.DestinationsNavTypeSerializer
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.base64ToByteArray
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.toBase64Str
import java.lang.reflect.Modifier

/**
 * Source https://github.com/raamcosta/compose-destinations
 * This is discouraged to be used
 * @property jClass Class<out Parcelable>
 * @property parcelableCreator Creator<T>
 * @property isFinal Boolean
 * @constructor
 */
class DefaultParcelableNavTypeSerializer(
    private val jClass: Class<out Parcelable>,
) : DestinationsNavTypeSerializer<Parcelable> {

    override fun toRouteString(value: Parcelable): String =
        value.javaClass.name + "@" + value.toBase64()

    override fun fromRouteString(routeStr: String): Parcelable {
        val (className, base64) = routeStr.split("@").let { it[0] to it[1] }

        val creator = if (jClass.isFinal) {
            // Since we have this, small optimization to avoid additional reflection call of Class.forName
            jClass.parcelableCreator
        } else {
            // If our class is not final, then we must use the actual class from "className"
            parcelableClassForName(className).parcelableCreator
        }

        return base64ToParcelable(base64, creator)
    }

    private fun Parcelable.toBase64(): String {
        val parcel = Parcel.obtain()
        writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle()

        return bytes.toBase64Str()
    }

    private fun <T> base64ToParcelable(base64: String, creator: Parcelable.Creator<T>): T {
        val bytes = base64.base64ToByteArray()
        val parcel = unmarshall(bytes)
        val result = creator.createFromParcel(parcel)
        parcel.recycle()
        return result
    }

    private fun unmarshall(bytes: ByteArray): Parcel {
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        return parcel
    }

    @Suppress("UNCHECKED_CAST")
    private val <T> Class<T>.parcelableCreator
        get(): Parcelable.Creator<T> {
            return try {
                val creatorField = getField("CREATOR")
                creatorField.get(null) as Parcelable.Creator<T>
            } catch (e: Exception) {
                throw BadParcelableException(e)
            } catch (t: Throwable) {
                throw BadParcelableException(t.message)
            }
        }

    @Suppress("UNCHECKED_CAST")
    private fun parcelableClassForName(className: String): Class<out Parcelable> {
        return Class.forName(className) as Class<out Parcelable>
    }

    private val Class<out Parcelable>.isFinal
        get() =
            !isInterface && Modifier.isFinal(modifiers)
}
