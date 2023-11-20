package dev.funkymuse.fosho.navigator.codegen.codegen.android

interface DestinationBlock {
    val name: String
    val isNullable: Boolean

    val prefix get() = if (isNullable) "?" else "/"
    val separator get() = if (isNullable) "&" else "/"
    val argument get() = if (isNullable) "$name={$name}" else "{$name}"
}