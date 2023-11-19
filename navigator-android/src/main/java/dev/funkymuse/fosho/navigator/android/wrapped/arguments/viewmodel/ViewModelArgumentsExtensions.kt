package dev.funkymuse.fosho.navigator.android.wrapped.arguments.viewmodel

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
context (ViewModelNavigationArguments)
fun getStringArgument(key: String): String? = DestinationsStringNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getBooleanArgument(key: String): Boolean? = DestinationsBooleanNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getIntArgument(key: String): Int? = DestinationsIntNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getLongArgument(key: String): Long? = DestinationsLongNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getFloatArgument(key: String): Float? = DestinationsFloatNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getFileArgument(key: String): File? = DestinationsFileNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getUriArgument(key: String): Uri? = DestinationsUriNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
inline fun <reified T : Enum<T>> getEnumArgument(key: String): T? =
    DestinationsEnumNavType(T::class.java).get(savedStateHandle, key)

context (ViewModelNavigationArguments)
inline fun <reified T : Parcelable> getParcelableArgument(key: String): T =
    DefaultParcelableNavTypeSerializer(T::class.java).fromRouteString(
        savedStateHandle.get<String>(key)
            ?: throw IllegalArgumentException("getParcelableArgument for $key is null"),
    ) as T

//array
context (ViewModelNavigationArguments)
fun getBooleanArrayArgument(key: String): BooleanArray? =
    DestinationsBooleanArrayNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getIntArrayArgument(key: String): IntArray? =
    DestinationsIntArrayNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getFloatArrayArgument(key: String): FloatArray? =
    DestinationsFloatArrayNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getLongArrayArgument(key: String): LongArray? =
    DestinationsLongArrayNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getStringArrayArgument(key: String): Array<String>? =
    DestinationsStringArrayNavType.get(savedStateHandle, key)

//serializable
context (ViewModelNavigationArguments)
fun getSerializable(key: String): Serializable =
    DefaultSerializableNavTypeSerializer.fromRouteString(
        savedStateHandle.get<String>(key) ?: throw IllegalArgumentException("getSerializable for $key is null"),
    )

//arraylist
context (ViewModelNavigationArguments)
fun getBooleanArrayListArgument(key: String): ArrayList<Boolean>? =
    DestinationsBooleanArrayListNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getFloatArrayListArgument(key: String): ArrayList<Float>? =
    DestinationsFloatArrayListNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getIntArrayListArgument(key: String): ArrayList<Int>? =
    DestinationsIntArrayListNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getLongArrayListArgument(key: String): ArrayList<Long>? =
    DestinationsLongArrayListNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
fun getStringArrayListArgument(key: String): ArrayList<String>? =
    DestinationsStringArrayListNavType.get(savedStateHandle, key)

context (ViewModelNavigationArguments)
inline fun <reified T : Enum<T>> getEnumArrayListArgument(key: String): ArrayList<T>? =
    DestinationsEnumArrayListNavType(T::class.java).get(savedStateHandle, key)
