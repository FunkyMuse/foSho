package dev.funkymuse.fosho.navigator.codegen.processor

import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.codegen.generators.MultiModuleGraphImplementationGenerator
import dev.funkymuse.fosho.navigator.codegen.codegen.graphFactoryGeneratorStream
import dev.funkymuse.fosho.navigator.codegen.codegen.plusAssign
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog
import dev.funkymuse.fosho.navigator.codegen.findArgumentValue
import dev.funkymuse.fosho.navigator.codegen.getArgumentValueByName
import dev.funkymuse.fosho.navigator.codegen.graphClassName
import dev.funkymuse.fosho.navigator.codegen.ksClassDeclaration
import dev.funkymuse.fosho.navigator.codegen.toClassNameInternalWithAddition
import dev.funkymuse.fosho.navigator.codegen.visitors.multi.MultiModuleContentVisitor
import dev.funkymuse.fosho.navigator.codegen.visitors.multi.MultiModuleGraphVisitor
import dev.funkymuse.fosho.navigator.codegen.visitors.multi.MultiModuleNavigationVisitor
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Codegen.localClassNameFor
import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.ClassNames.KotlinXImmutable.toImmutableList
import dev.funkymuse.fosho.navigator.codegen.annotation.AggregatorContent
import dev.funkymuse.fosho.navigator.codegen.annotation.AggregatorGraph
import dev.funkymuse.fosho.navigator.codegen.annotation.Content
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.classMustBeInternalVisibility
import dev.funkymuse.fosho.navigator.codegen.codegen.addGraphsVariable
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet
import dev.funkymuse.fosho.navigator.codegen.contract.Screen
import dev.funkymuse.fosho.navigator.codegen.visitors.single.SingleModuleGraphVisitor
import dev.funkymuse.fosho.navigator.codegen.visitors.single.SingleModuleNavigationVisitor

internal class NavigationProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val isSingleModule: Boolean,
) : SymbolProcessor {
    private val navigationDestinationClassName = localClassNameFor(Constants.NavigationDestination)
    private val androidDialogType = localClassNameFor(Constants.AndroidDialog)
    private val androidScreenType = localClassNameFor(Constants.AndroidScreen)
    private val androidBottomSheetType = localClassNameFor(Constants.AndroidBottomSheet)
    private val androidGraphType = localClassNameFor(Constants.AndroidGraph)

    override fun process(resolver: Resolver): List<KSAnnotated> =
        if (isSingleModule) {
            val navRequestSymbol = singleModuleNavRequestSymbolResolver(resolver = resolver)
            singleModuleGraphsResolver(resolver)

            navRequestSymbol.filterNot { declaration -> declaration.validate() }.toList()
        } else {
            val navRequestSymbol = multiModuleNavRequestSymbolResolver(
                resolver = resolver
            )
            multiModuleContentResolver(resolver)
            multiModuleGraphsResolver(resolver)
            multiModuleAggregateResolver(resolver)
            navRequestSymbol.filterNot { declaration -> declaration.validate() }.toList()
        }


    private var generated = false

    @OptIn(KspExperimental::class)
    private fun multiModuleAggregateResolver(resolver: Resolver) {
        if (moduleName(resolver) == Constants.app == !generated) {
            val declarations = resolver.getDeclarationsFromPackage(LOCAL_PATH).toList()
            val aggregatorGraphs =
                declarations.filter { it.isAnnotationPresent(AggregatorGraph::class) }
            val content =
                declarations.filter { it.isAnnotationPresent(AggregatorContent::class) }

            val bottomSheet = resolver.getClassDeclarationByName<BottomSheet>()
            val dialog = resolver.getClassDeclarationByName<Dialog>()
            val screen = resolver.getClassDeclarationByName<Screen>()

            val contentMap = content.associateBy { ksDeclaration ->
                val annotations = (ksDeclaration as KSAnnotated).annotations.first()
                val forContent = annotations.findArgumentValue<KSType>(Constants.forContent)
                val forContentDestinationType =
                    forContent.ksClassDeclaration().getAllSuperTypes().first()

                forContentDestinationType.ksClassDeclaration().toClassName().simpleName
            }

            val graphFactory = graphFactoryGeneratorStream(codeGenerator)
            graphFactory += "{${addGraphsVariable()}"

            val rootGraph = aggregatorGraphs.first {
                (it as KSAnnotated).annotations.first().findArgumentValue(Constants.rootGraph)
            } as KSClassDeclaration

            aggregatorGraphs.forEachIndexed { index, aggregatorGraphAnnotation ->
                val annotations = (aggregatorGraphAnnotation as KSAnnotated).annotations.first()
                val destinationsWithoutStarting =
                    annotations.findArgumentValue<List<KSType>>(Constants.destinations)
                val startingDestination =
                    annotations.findArgumentValue<KSType>(Constants.startingDestination)
                val destinations = destinationsWithoutStarting.plus(startingDestination)

                val graphName = graphClassName(aggregatorGraphAnnotation as KSClassDeclaration)

                val file = FileSpec.builder(
                    packageName = LOCAL_PATH,
                    fileName = graphName
                )

                val destinationTypes = destinations.map {
                    val kClass = it.ksClassDeclaration()
                    kClass to kClass.getAllSuperTypes().first().ksClassDeclaration()
                }.distinct()

                val screens = getTypeDeclarations(destinationTypes, screen, contentMap)
                val dialogs = getTypeDeclarations(destinationTypes, dialog, contentMap)
                val bottomSheets =
                    getTypeDeclarations(destinationTypes, bottomSheet, contentMap)

                val startingDestinationDeclaration =
                    contentMap[startingDestination.ksClassDeclaration()
                        .toClassNameInternalWithAddition(Constants.Destination).simpleName] as? KSClassDeclaration

                if (startingDestinationDeclaration == null) {
                    warnContentNotPresent(startingDestination.ksClassDeclaration())
                } else {
                    val graphType = MultiModuleGraphImplementationGenerator(
                        screens = screens,
                        dialogs = dialogs,
                        bottomSheets = bottomSheets,
                        startingDestinationDeclaration = startingDestinationDeclaration,
                        dialogType = androidDialogType,
                        screenType = androidScreenType,
                        bottomSheetType = androidBottomSheetType,
                        navigationDestinationClassName = navigationDestinationClassName,
                        androidGraphClassName = androidGraphType,
                    ).createMultiModuleGraph(
                        aggregatorGraphAnnotation,
                    )
                    file.addType(graphType.build())
                    file.build().writeTo(codeGenerator, true)

                    graphFactory += "\t\t${graphClassName(aggregatorGraphAnnotation)},\n"
                }

                if (index == aggregatorGraphs.lastIndex) {
                    graphFactory += "\t).$toImmutableList()"
                }

                generated = true
            }

            graphFactory += "\n\n"
            graphFactory += "\tval ${Constants.rootGraph} = ${graphClassName(rootGraph)}"
            graphFactory += "\n\n}"


            graphFactory.close()
        }
    }

    private fun warnContentNotPresent(ksClassDeclaration: KSClassDeclaration) {
        logger.warn(
            "$ksClassDeclaration must have an associated content implementation to show the screen contents," +
                    "proceed creating a content owner or you won't see this destination in the aggregated ${Constants.GraphFactory}.\n" +
                    "For example you can use ${ksClassDeclaration}Content as an object name, make sure to create one, you can use the following code:\n" +
                    "@${Constants.Content}\n" +
                    "object ${ksClassDeclaration}${Constants.Content} : ${ksClassDeclaration}${Constants.Destination} {" +
                    "\n" +
                    "@Composable\n" +
                    "override fun ${Constants.Content}(){ \n" +
                    "TODO(\"NOT IMPLEMENTED\")\n" +
                    "}" +
                    "\n" +
                    "}"
        )
    }

    private fun getTypeDeclarations(
        destinationTypes: List<Pair<KSClassDeclaration, KSClassDeclaration>>,
        screenTemplate: KSClassDeclaration?,
        contentMap: Map<String, KSDeclaration>
    ) = destinationTypes.filter { it.second == screenTemplate }.map { it.first }.mapNotNull {
        contentMap[it.toClassNameInternalWithAddition(Constants.Destination).simpleName] as? KSClassDeclaration
    }

    private fun moduleName(resolver: Resolver): String {
        val moduleDescriptor = resolver::class.java
            .getDeclaredField("module")
            .apply { isAccessible = true }
            .get(resolver)
        val rawName = moduleDescriptor::class.java
            .getMethod("getName")
            .invoke(moduleDescriptor)
            .toString()
        return rawName.removeSurrounding("<", ">").substringBeforeLast("_")
    }

    private fun singleModuleGraphsResolver(resolver: Resolver) {
        val graphs =
            resolver.getSymbolsWithAnnotation(Graph::class.qualifiedName.toString()).toList()
        val content =
            resolver.getSymbolsWithAnnotation(Content::class.qualifiedName.toString()).toList()
        if (graphs.isNotEmpty() && content.isNotEmpty() && !generated) {

            val contentDeclaration = content.filterIsInstance<KSClassDeclaration>()

            contentDeclaration.forEach { classDeclaration ->
                if (classDeclaration.classKind != ClassKind.OBJECT) {
                    logger.error(
                        "Only object can be annotated with @${Content::class.java.simpleName}",
                        classDeclaration
                    )
                    return
                }
                classDeclaration.classMustBeInternalVisibility(logger)
            }

            val graphSymbol = graphs.filterIsInstance<KSClassDeclaration>()
            val singleModuleGraphVisitor = SingleModuleGraphVisitor(
                logger = logger,
                codeGenerator = codeGenerator,
                resolver = resolver,
                screenNavigationDestinationClassName = androidScreenType,
                dialogNavigationDestinationClassName = androidDialogType,
                bottomSheetNavigationDestinationClassName = androidBottomSheetType,
                navigationDestinationClassName = navigationDestinationClassName,
                androidGraphClassName = androidGraphType,
            )

            val rootGraphCount =
                graphs.count { it.annotations.getArgumentValueByName(Constants.rootGraph) }

            require(rootGraphCount != 0) {
                "There must be at least one ${Constants.rootGraph}"
            }

            require(rootGraphCount == 1) {
                "There can be only one ${Constants.rootGraph}"
            }

            val rootGraph = graphs.first {
                it.annotations.first().findArgumentValue(Constants.rootGraph)
            } as KSClassDeclaration


            val graphFactory = graphFactoryGeneratorStream(codeGenerator)
            graphFactory += "{${addGraphsVariable()}"

            graphSymbol.forEachIndexed { index, ksClassDeclaration ->
                singleModuleGraphVisitor.visitClassDeclaration(
                    ksClassDeclaration,
                    contentDeclaration
                )
                graphFactory += "\t\t${graphClassName(ksClassDeclaration)},\n"
                if (index == graphSymbol.lastIndex){
                    graphFactory += "\t).$toImmutableList()"
                }
            }

            graphFactory += "\n\n"
            graphFactory += "\tval ${Constants.rootGraph} = ${graphClassName(rootGraph)}"
            graphFactory += "\n\n}"


            graphFactory.close()

            generated = true
        }
    }


    private fun multiModuleGraphsResolver(resolver: Resolver) {
        val graphs = resolver.getSymbolsWithAnnotation(Graph::class.qualifiedName.toString())

        if (graphs.count() > 0) {
            val graphSymbol = graphs.filterIsInstance<KSClassDeclaration>()
            val multiModuleGraphVisitor = MultiModuleGraphVisitor(
                logger = logger,
                codeGenerator = codeGenerator,
                resolver = resolver,
                screenNavigationDestinationClassName = androidScreenType,
                dialogNavigationDestinationClassName = androidDialogType,
                bottomSheetNavigationDestinationClassName = androidBottomSheetType,
                navigationDestinationClassName = navigationDestinationClassName,
                androidGraphClassName = androidGraphType,
            )

            val rootGraphCount =
                graphs.count { it.annotations.getArgumentValueByName(Constants.rootGraph) }

            require(rootGraphCount != 0) {
                "There must be at least one ${Constants.rootGraph}"
            }

            require(rootGraphCount == 1) {
                "There can be only one ${Constants.rootGraph}"
            }

            graphSymbol.forEach { ksClassDeclaration ->
                ksClassDeclaration.accept(multiModuleGraphVisitor, Unit)
            }
        }
    }

    private fun multiModuleContentResolver(resolver: Resolver) {
        val content = resolver.getSymbolsWithAnnotation(Content::class.qualifiedName.toString())
        if (content.count() > 0 && content.iterator().hasNext()) {
            val contentSymbol = content.filterIsInstance<KSClassDeclaration>()
            val multiModuleContentVisitor = MultiModuleContentVisitor(
                logger = logger,
                codeGenerator = codeGenerator,
                resolver = resolver
            )
            contentSymbol.forEach { ksClassDeclaration ->
                ksClassDeclaration.accept(multiModuleContentVisitor, Unit)
            }
        }
    }

    private fun multiModuleNavRequestSymbolResolver(resolver: Resolver): Sequence<KSClassDeclaration> {
        val navigationRequestSymbol: Sequence<KSAnnotated> = resolver.getSymbolsWithAnnotation(
            Destination::class.qualifiedName.toString()
        )

        val navRequestSymbol = navigationRequestSymbol.filterIsInstance<KSClassDeclaration>()

        val multiModuleNavigationVisitor = MultiModuleNavigationVisitor(
            resolver = resolver,
            logger = logger,
            codeGenerator = codeGenerator,
            screenNavigationDestinationClassName = androidScreenType,
            dialogNavigationDestinationClassName = androidDialogType,
            bottomSheetNavigationDestinationClassName = androidBottomSheetType,
        )

        navRequestSymbol.forEach { ksClassDeclaration ->
            logger.info("Processing symbol...", ksClassDeclaration)
            ksClassDeclaration.accept(multiModuleNavigationVisitor, Unit)
        }
        return navRequestSymbol
    }

    private fun singleModuleNavRequestSymbolResolver(resolver: Resolver): Sequence<KSClassDeclaration> {
        val navigationRequestSymbol: Sequence<KSAnnotated> = resolver.getSymbolsWithAnnotation(
            Destination::class.qualifiedName.toString()
        )

        val navRequestSymbol = navigationRequestSymbol.filterIsInstance<KSClassDeclaration>()

        val multiModuleNavigationVisitor = SingleModuleNavigationVisitor(
            resolver = resolver,
            logger = logger,
            codeGenerator = codeGenerator,
            screenNavigationDestinationClassName = androidScreenType,
            dialogNavigationDestinationClassName = androidDialogType,
            bottomSheetNavigationDestinationClassName = androidBottomSheetType,
        )

        navRequestSymbol.forEach { ksClassDeclaration ->
            logger.info("Processing symbol...", ksClassDeclaration)
            ksClassDeclaration.accept(multiModuleNavigationVisitor, Unit)
        }
        return navRequestSymbol
    }


}
