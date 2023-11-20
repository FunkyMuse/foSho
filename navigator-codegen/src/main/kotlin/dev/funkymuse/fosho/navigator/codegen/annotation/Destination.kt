package dev.funkymuse.fosho.navigator.codegen.annotation

@Target(AnnotationTarget.CLASS)
annotation class Destination(
    val generateViewModelArguments: Boolean = false,
    val generateScreenEntryArguments: Boolean = false
)

