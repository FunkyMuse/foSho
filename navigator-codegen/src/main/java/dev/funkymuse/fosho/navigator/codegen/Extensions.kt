package dev.funkymuse.fosho.navigator.codegen

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

internal val KSType.packageName: String get() = declaration.packageName.asString()

internal fun KSClassDeclaration.getAnnotation(
    annotationKClass: KClass<*>
): Sequence<KSAnnotation> =
     annotations.findAnnotation(annotationKClass)


internal fun Sequence<KSAnnotation>.findAnnotation(annotationKClass: KClass<*>) = filter {
    it.annotationType
        .resolve()
        .declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
}

internal inline fun <reified T> KSAnnotation.findArgumentValue(name: String): T = arguments.first {
    it.name?.asString() == name
}.value as T

fun KSType.ksClassDeclaration() =
    (declaration as KSClassDeclaration)
internal inline fun <reified T> Sequence<KSAnnotation>.getArgumentValueByName(name: String) = first().findArgumentValue<T>(name)


