package dev.funkymuse.fosho.navigator.android

import android.net.Uri
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.funkymuse.fosho.navigator.codegen.codegen.android.DestinationArgument
import dev.funkymuse.fosho.navigator.codegen.codegen.android.DestinationValue
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentContract
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers.DestinationsFileNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.helpers.DestinationsUriNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsBooleanNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsFloatNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsIntNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsLongNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.DestinationsStringNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsBooleanArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsFloatArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsIntArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsLongArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.array.DestinationsStringArrayNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsBooleanArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsFloatArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsIntArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsLongArrayListNavType
import dev.funkymuse.fosho.navigator.android.wrapped.navargs.primitives.arraylist.DestinationsStringArrayListNavType
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.argument.DeepLinkContract
import java.io.File

fun toNavArgument(argument: ArgumentContract): NamedNavArgument =
    navArgument(argument.name) {
        if (!(!argument.isNullable && argument.defaultValue == null)) {
            nullable = argument.isNullable
            defaultValue = argument.defaultValue
        }
        type = when (argument.argumentType) {
            ArgumentType.INT -> DestinationsIntNavType
            ArgumentType.STRING -> DestinationsStringNavType
            ArgumentType.BOOLEAN -> DestinationsBooleanNavType
            ArgumentType.FLOAT -> DestinationsFloatNavType
            ArgumentType.LONG -> DestinationsLongNavType
            ArgumentType.BOOLEAN_ARRAY -> DestinationsBooleanArrayNavType
            ArgumentType.FLOAT_ARRAY -> DestinationsFloatArrayNavType
            ArgumentType.INT_ARRAY -> DestinationsIntArrayNavType
            ArgumentType.LONG_ARRAY -> DestinationsLongArrayNavType
            ArgumentType.STRING_ARRAY -> DestinationsStringArrayNavType
            ArgumentType.BOOLEAN_ARRAY_LIST -> DestinationsBooleanArrayListNavType
            ArgumentType.FLOAT_ARRAY_LIST -> DestinationsFloatArrayListNavType
            ArgumentType.INT_ARRAY_LIST -> DestinationsIntArrayListNavType
            ArgumentType.LONG_ARRAY_LIST -> DestinationsLongArrayListNavType
            ArgumentType.STRING_ARRAY_LIST -> DestinationsStringArrayListNavType
            ArgumentType.URI -> DestinationsUriNavType
            ArgumentType.FILE -> DestinationsFileNavType
        }
    }

fun dialogPropertiesToNavProperties(template: Dialog) = template.run {
    DialogProperties(
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        securePolicy = when (securePolicy) {
            Dialog.SecureFlagPolicyTemplate.Inherit -> SecureFlagPolicy.Inherit
            Dialog.SecureFlagPolicyTemplate.SecureOn -> SecureFlagPolicy.SecureOn
            Dialog.SecureFlagPolicyTemplate.SecureOff -> SecureFlagPolicy.SecureOff
        },
        usePlatformDefaultWidth = usePlatformDefaultWidth,
        decorFitsSystemWindows = decorFitsSystemWindows
    )
}

fun toDeepLink(argument: DeepLinkContract): NavDeepLink = navDeepLink {
    action = argument.action
    uriPattern = argument.uriPattern
    mimeType = argument.mimeType
}

fun toDestinationRoute(route: String, arguments: List<ArgumentContract>): String {
    if (arguments.isEmpty()) return route

    return createDestination(route, *arguments.map { namedNavArgument ->
        namedNavArgument.toDestinationArgument()
    }.toTypedArray())
}

private fun ArgumentContract.toDestinationArgument(): DestinationArgument =
    DestinationArgument(name = name, isNullable = isNullable)

fun toOpenFunction(arguments: List<ArgumentContract>, route: String): String {
    if (arguments.isEmpty()) return route

    return applyArgumentsToDestination(route, *arguments.map { namedNavArgument ->
        namedNavArgument.toDestinationValue()
    }.toTypedArray())
}

@Suppress("UNCHECKED_CAST")
private fun ArgumentContract.toDestinationValue(newValue: Any? = defaultValue): DestinationValue =
    DestinationValue(
        name = name, value = when (newValue) {
            is Int? -> DestinationsIntNavType.serializeValue(newValue)
            is String? -> DestinationsStringNavType.serializeValue(newValue)
            is Boolean? -> DestinationsBooleanNavType.serializeValue(newValue)
            is Float? -> DestinationsFloatNavType.serializeValue(newValue)
            is Long? -> DestinationsLongNavType.serializeValue(newValue)
            is BooleanArray? -> DestinationsBooleanArrayNavType.serializeValue(newValue)
            is FloatArray? -> DestinationsFloatArrayNavType.serializeValue(newValue)
            is IntArray? -> DestinationsIntArrayNavType.serializeValue(newValue)
            is LongArray? -> DestinationsLongArrayNavType.serializeValue(newValue)
            is Array<*>? -> {
                val isStringArray = newValue?.isArrayOf<String>()
                if (isStringArray == true) {
                    DestinationsStringArrayNavType.serializeValue(newValue as? Array<String>)
                } else {
                    DestinationsStringArrayNavType.serializeValue(null)
                }
            }

            is List<*>? -> {
                val valueArray = newValue?.toTypedArray()
                when {
                    valueArray?.isArrayOf<Boolean>() == true -> DestinationsBooleanArrayListNavType.serializeValue(
                        (newValue as? List<Boolean>)?.let { ArrayList(it) }
                    )

                    valueArray?.isArrayOf<Float>() == true -> DestinationsFloatArrayListNavType.serializeValue(
                        (newValue as? List<Float>)?.let { ArrayList(it) }
                    )

                    valueArray?.isArrayOf<Int>() == true -> DestinationsIntArrayListNavType.serializeValue(
                        (newValue as? List<Int>)?.let { ArrayList(it) }
                    )

                    valueArray?.isArrayOf<Long>() == true -> DestinationsLongArrayListNavType.serializeValue(
                        (newValue as? List<Long>)?.let { ArrayList(it) }
                    )

                    valueArray?.isArrayOf<String>() == true -> DestinationsStringArrayListNavType.serializeValue(
                        (newValue as? List<String>)?.let { ArrayList(it) }
                    )

                    else -> null
                }
            }

            is Uri? -> DestinationsUriNavType.serializeValue(newValue)
            is File? -> DestinationsFileNavType.serializeValue(newValue)
            else -> throw IllegalArgumentException("Unsupported type $defaultValue, check ArgumentType for supported types!")
        }, isNullable = isNullable
    )


internal fun createDestination(route: String, vararg arguments: DestinationArgument): String =
    route
        .plus(
            arguments.toList().joinToStringInternal(
                separator = { index ->
                    arguments[index].separator
                },
                prefix = arguments.first().prefix,
                transform = { destinationArgument ->
                    destinationArgument.argument
                }
            )
        )

internal fun applyArgumentsToDestination(route: String, vararg arguments: DestinationValue): String =
    route
        .plus(
            arguments.toList().joinToStringInternal(
                separator = { index -> arguments[index].separator },
                prefix = arguments.first().prefix,
                transform = { destinationArgument ->
                    destinationArgument.destinationValue
                }
            )
        )

private fun <T> Iterable<T>.joinToStringInternal(
    separator: (Int) -> CharSequence,
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String =
    joinToInternal(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()


private fun <T, A : Appendable> Iterable<T>.joinToInternal(
    buffer: A,
    separator: (Int) -> CharSequence,
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    buffer.append(prefix)
    var count = 0
    var index = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator(index))
        if (limit < 0 || count <= limit) {
            buffer.appendElementInternal(element, transform)
            index++
        } else break
    }
    if ((limit >= 0) && (count > limit)) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}


private fun <T> Appendable.appendElementInternal(element: T, transform: ((T) -> CharSequence)?) {
    when {
        transform != null -> append(transform(element))
        element is CharSequence? -> append(element)
        element is Char -> append(element)
        else -> append(element.toString())
    }
}
