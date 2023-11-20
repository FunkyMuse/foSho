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

internal class MultiModuleGraphImplementationGenerator(
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
    private val completeDestinations = screens.plus(dialogs).plus(bottomSheets)
        .plus(startingDestinationDeclaration).distinct()

    fun createAggregatorGraph(declaration: KSClassDeclaration, isRootGraph: Boolean) =
        TypeSpec
            .interfaceBuilder(aggregatorClassName(declaration))
            .addAnnotation(
                AnnotationSpec.builder(AggregatorGraph::class)
                    .addMember("%L = %L", Constants.rootGraph, isRootGraph)
                    .addMember(buildCodeBlock {
                        add("%L = ", Constants.startingDestination)
                        add(
                            "%T${Constants.SCOPED_CLASS}",
                            startingDestinationDeclaration.toClassName()
                        )
                    })
                    .addMember(buildCodeBlock {
                        add("%L = [%L]", Constants.destinations,
                            completeDestinations
                                .joinToString {
                                    it.toClassName().toString().plus(Constants.SCOPED_CLASS)
                                })
                    })
                    .build()
            )
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
                                add("%S", "${declaration.qualifiedName?.getShortName()}")
                            })
                            .build()
                    )
                    .build()
            ).addType(
                TypeSpec.companionObjectBuilder()
                    .addSuperinterface(ClassName.bestGuess(aggregatorClassName(declaration)))
                    .addProperty(
                        PropertySpec.builder(
                            name = Constants.startingDestination,
                            type = navigationDestinationClassName,
                            KModifier.OVERRIDE
                        )
                            .initializer(
                                "%L",
                                buildCodeBlock {
                                    add(
                                        "${Constants.`object`} : ${Constants.NavigationDestination} {\n" +
                                                "override val ${Constants.destination}: String\n" +
                                                "   get() = ${Constants.`throw`} ${Constants.IllegalArgumentException}(\"STUB\")\n" +
                                                "}"
                                    )
                                })
                            .build()
                    ).build()
            )

    private fun aggregatorClassName(declaration: KSClassDeclaration) =
        "${declaration.qualifiedName?.getShortName()}${Constants.Stub}"

    fun createMultiModuleGraph(declaration: KSClassDeclaration) =
        TypeSpec
            .objectBuilder(graphClassName(declaration))
            .addKdoc(generatedFromDocs(declaration))
            .addSuperinterface(ClassName.bestGuess(declaration.simpleName.asString() + "Stub"))
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