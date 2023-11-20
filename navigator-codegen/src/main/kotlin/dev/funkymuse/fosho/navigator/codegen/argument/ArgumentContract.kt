package dev.funkymuse.fosho.navigator.codegen.argument

data class ArgumentContract(
    val name: String,
    val argumentType: ArgumentType,
    val defaultValue: Any? = null,
    val isNullable: Boolean = false,
)

