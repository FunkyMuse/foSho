package dev.funkymuse.fosho.navigator.android.wrapped.arguments.nav_entry

import android.net.Uri
import android.os.Parcelable
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers.DestinationsFileNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers.DestinationsUriNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.parcelable.DefaultParcelableNavTypeSerializer
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsIntNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsBooleanArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsFloatArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsIntArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsLongArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsStringArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsBooleanNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsEnumNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsFloatNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsLongNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsStringNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsBooleanArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsEnumArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsFloatArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsIntArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsLongArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsStringArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.serializable.DefaultSerializableNavTypeSerializer
import java.io.File
import java.io.Serializable

//primitives
context (NavigationEntryArguments)
fun getStringArgument(key: String): String? = DestinationsStringNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getBooleanArgument(key: String): Boolean? = DestinationsBooleanNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getIntArgument(key: String): Int? = DestinationsIntNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getLongArgument(key: String): Long? = DestinationsLongNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getFloatArgument(key: String): Float? = DestinationsFloatNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getFileArgument(key: String): File? = DestinationsFileNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getUriArgument(key: String): Uri? = DestinationsUriNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
inline fun <reified T : Enum<T>> getEnumArgument(key: String): T? =
    DestinationsEnumNavType(T::class.java).safeGet(arguments, key)

context (NavigationEntryArguments)
inline fun <reified T : Parcelable> getParcelableArgument(key: String): T =
    DefaultParcelableNavTypeSerializer(T::class.java).fromRouteString(
        arguments?.getString(key)
            ?: throw IllegalArgumentException("getParcelableArgument for $key is null"),
    ) as T

//array
context (NavigationEntryArguments)
fun getBooleanArrayArgument(key: String): BooleanArray? =
    DestinationsBooleanArrayNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getIntArrayArgument(key: String): IntArray? =
    DestinationsIntArrayNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getFloatArrayArgument(key: String): FloatArray? =
    DestinationsFloatArrayNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getLongArrayArgument(key: String): LongArray? =
    DestinationsLongArrayNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getStringArrayArgument(key: String): Array<String>? =
    DestinationsStringArrayNavType.safeGet(arguments, key)

//serializable
context (NavigationEntryArguments)
fun getSerializable(key: String): Serializable =
    DefaultSerializableNavTypeSerializer.fromRouteString(
        arguments?.getString(key) ?: throw IllegalArgumentException("getSerializable for $key is null"),
    )

//arraylist
context (NavigationEntryArguments)
fun getBooleanArrayListArgument(key: String): ArrayList<Boolean>? =
    DestinationsBooleanArrayListNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getFloatArrayListArgument(key: String): ArrayList<Float>? =
    DestinationsFloatArrayListNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getIntArrayListArgument(key: String): ArrayList<Int>? =
    DestinationsIntArrayListNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getLongArrayListArgument(key: String): ArrayList<Long>? =
    DestinationsLongArrayListNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
fun getStringArrayListArgument(key: String): ArrayList<String>? =
    DestinationsStringArrayListNavType.safeGet(arguments, key)

context (NavigationEntryArguments)
inline fun <reified T : Enum<T>> getEnumArrayListArgument(key: String): ArrayList<T>? =
    DestinationsEnumArrayListNavType(T::class.java).safeGet(arguments, key)
