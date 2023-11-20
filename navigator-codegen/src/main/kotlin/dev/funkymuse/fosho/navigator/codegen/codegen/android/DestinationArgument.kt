package dev.funkymuse.fosho.navigator.codegen.codegen.android

data class DestinationArgument(
    override val name: String,
    override val isNullable: Boolean = false
) : DestinationBlock