package dev.funkymuse.fosho.navigator.codegen

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

internal fun deepLinksTemplate(screenClass: ClassName) = "${screenClass.simpleName}.deepLinksList"


internal fun KSDeclaration.toClassNameInternalWithAddition(addition : String = ""): ClassName {
    require(!isLocal()) {
        "Local/anonymous classes are not supported!"
    }
    val pkgName = packageName.asString()
    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.").plus(addition)

    val simpleNames = typesString
        .split(".")
    return ClassName(pkgName, simpleNames)
}

internal fun KSClassDeclaration.classMustBeInternalVisibility(logger : KSPLogger) {
    if (getVisibility() != Visibility.INTERNAL) {
        logger.error("${toClassName()} must be internal")
    }
}

internal fun graphClassName(declaration: KSClassDeclaration) =
    "${declaration.qualifiedName?.getShortName()?.replace(Constants.Stub, "")?.replace(
        Constants.Graph,
        Constants.NavigationGraph
    )}"

internal enum class UIType {
    BOTTOM_SHEET, DIALOG, SCREEN
}