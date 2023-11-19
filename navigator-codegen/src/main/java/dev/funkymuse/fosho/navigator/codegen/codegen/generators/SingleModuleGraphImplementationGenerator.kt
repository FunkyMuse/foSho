package dev.funkymuse.fosho.navigator.codegen.codegen.generators

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.annotation.AggregatorGraph
import dev.funkymuse.fosho.navigator.codegen.codegen.generatedFromDocs
import dev.funkymuse.fosho.navigator.codegen.graphClassName

internal class SingleModuleGraphImplementationGenerator(
    private val screens: List<KSClassDeclaration>,
    private val dialogs: List<KSClassDeclaration>,
    private val bottomSheets: List<KSClassDeclaration>,
    private val startingDestinationDeclaration: KSClassDeclaration,
    private val dialogType: ClassName,
    private val screenType: ClassName,
    private val bottomSheetType: ClassName,
    private val navigationDestinationClassName: ClassName,
    private val androidGraphClassName: ClassName,
) {

    fun createGraph(declaration: KSClassDeclaration) = TypeSpec
        .objectBuilder(graphClassName(declaration))
        .addKdoc(generatedFromDocs(declaration))
        .addSuperinterface(androidGraphClassName)
        .addProperty(
            PropertySpec.builder(
                name = Constants.route,
                type = String::class,
                KModifier.OVERRIDE
            )
                .getter(
                    FunSpec.getterBuilder()
                        .addCode(buildCodeBlock {
                            add("%L", "${Constants.`return`} ")
                            add("%S", graphClassName(declaration))
                        })
                        .build()
                )
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                name = Constants.screens,
                type = List::class.asClassName().parameterizedBy(screenType),
                KModifier.OVERRIDE
            )
                .initializer(
                    "%L",
                    buildCodeBlock {
                        createList(screens.map { it.toClassName() })
                    })
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                name = Constants.dialogs,
                type = List::class.asClassName().parameterizedBy(dialogType),
                KModifier.OVERRIDE
            )
                .initializer(
                    "%L",
                    buildCodeBlock {
                        createList(dialogs.map { it.toClassName() })
                    })
                .build()
        ).addProperty(
            PropertySpec.builder(
                name = Constants.bottomSheets,
                type = List::class.asClassName().parameterizedBy(bottomSheetType),
                KModifier.OVERRIDE
            )
                .initializer(
                    "%L",
                    buildCodeBlock {
                        createList(bottomSheets.map { it.toClassName() })
                    })
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                name = Constants.startingDestination,
                type = navigationDestinationClassName,
                KModifier.OVERRIDE
            )
                .initializer(
                    "%L",
                    buildCodeBlock {
                        add(startingDestinationDeclaration.toClassName().toString())
                    })
                .build()
        )

    private fun CodeBlock.Builder.createList(
        otherDestinations: List<ClassName>,
    ) {
        var listRegex = otherDestinations.toString()
        listRegex = listRegex.replace("[", "")
        listRegex = listRegex.replace("]", "")
        add("${Constants.listOf}($listRegex)")
    }


}