package dev.funkymuse.fosho.navigator.codegen.annotation

import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentValue
import dev.funkymuse.fosho.navigator.codegen.argument.DefaultArgumentValue
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class Argument(
    val name: String,
    val argumentType: ArgumentType,
    val defaultValue: KClass<out ArgumentValue> = DefaultArgumentValue::class,
    val isNullable: Boolean = false
)