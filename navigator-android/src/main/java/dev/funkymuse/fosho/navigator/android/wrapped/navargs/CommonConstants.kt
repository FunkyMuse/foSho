package dev.funkymuse.fosho.navigator.android.wrapped.navargs
/**
 * Source https://github.com/raamcosta/compose-destinations
 *
 **/
const val ENCODED_NULL = "%02null%03"
const val DECODED_NULL: String = "\u0002null\u0003"

const val encodedComma = "%2C"

internal const val ENCODED_EMPTY_STRING = "%02%03"
internal const val DECODED_EMPTY_STRING: String = "\u0002\u0003"

internal const val ENCODED_DEFAULT_VALUE_STRING_PREFIX = "%02def%03"
internal const val DECODED_DEFAULT_VALUE_STRING_PREFIX: String = "\u0002def\u0003"

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun <E : Enum<*>> Class<E>.valueOfIgnoreCase(enumValueName: String): E {
    return enumConstants.firstOrNull { constant ->
        constant.name.equals(enumValueName, ignoreCase = true)
    } ?: throw IllegalArgumentException(
        "Enum value $enumValueName not found for type ${this.name}.",
    )
}

fun String?.orNullArgument(): String = this ?: DECODED_NULL
