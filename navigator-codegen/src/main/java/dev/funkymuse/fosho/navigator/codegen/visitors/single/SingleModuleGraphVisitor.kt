package dev.funkymuse.fosho.navigator.codegen.visitors.single

import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.classMustBeInternalVisibility
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog
import dev.funkymuse.fosho.navigator.codegen.findArgumentValue
import dev.funkymuse.fosho.navigator.codegen.getAnnotation
import dev.funkymuse.fosho.navigator.codegen.ksClassDeclaration
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import dev.funkymuse.fosho.navigator.codegen.codegen.generators.SingleModuleGraphImplementationGenerator
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet
import dev.funkymuse.fosho.navigator.codegen.contract.Screen
import dev.funkymuse.fosho.navigator.codegen.graphClassName

internal class SingleModuleGraphVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val screenNavigationDestinationClassName: ClassName,
    private val dialogNavigationDestinationClassName: ClassName,
    private val bottomSheetNavigationDestinationClassName: ClassName,
    private val navigationDestinationClassName: ClassName,
    private val androidGraphClassName: ClassName,
) {

    fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        content: List<KSClassDeclaration>
    ) {
        if (classDeclaration.classKind != ClassKind.OBJECT) {
            logger.error(
                "Only object can be annotated with @${Graph::class.java.simpleName}",
                classDeclaration
            )
            return
        }
        classDeclaration.classMustBeInternalVisibility(logger)

        val graphs = classDeclaration.getAnnotation(Graph::class)
        val graph = graphs.first()

        val startingDestination = graph.findArgumentValue<KSType>(Constants.startingDestination)

        val bottomSheet = resolver.getClassDeclarationByName<BottomSheet>()
        val dialog = resolver.getClassDeclarationByName<Dialog>()
        val screen = resolver.getClassDeclarationByName<Screen>()


        val destinationsWithoutStarting =
            graph.findArgumentValue<List<KSType>>(Constants.destinations)

        require(destinationsWithoutStarting.none { ksType -> ksType.ksClassDeclaration().classKind != ClassKind.OBJECT }) {
            "Screen destinations must be objects, not classes!"
        }

        require(destinationsWithoutStarting.none { ksType -> ksType == startingDestination }) {
            "Starting destination must not be added inside destinations"
        }

        val destinations = destinationsWithoutStarting + startingDestination

        val contentMap = content.associateBy { it.superTypes.first().toString() }

        val destinationsMap = destinations.associateWith { destination ->
            contentMap[destination.ksClassDeclaration().simpleName.asString()
                .plus(Constants.Destination)]
        }

        val startingDestinationContent = destinationsMap[startingDestination] ?: throw IllegalArgumentException("Starting destination ${startingDestination.ksClassDeclaration()} is not present")
        val screens = getDestinationType(destinationsMap, screen)
        val dialogs = getDestinationType(destinationsMap, dialog)
        val bottomSheets = getDestinationType(destinationsMap, bottomSheet)

        val file = FileSpec.builder(
            packageName = LOCAL_PATH,
            fileName = graphClassName(classDeclaration)
        )
        val sourceFile = requireNotNull(graph.containingFile) {
            "Unable to find sourceFile for ${classDeclaration.qualifiedName?.getShortName()}"
        }

        val graphType = SingleModuleGraphImplementationGenerator(
            screens = screens,
            dialogs = dialogs,
            bottomSheets = bottomSheets,
            startingDestinationDeclaration = startingDestinationContent,
            dialogType = dialogNavigationDestinationClassName,
            screenType = screenNavigationDestinationClassName,
            bottomSheetType = bottomSheetNavigationDestinationClassName,
            navigationDestinationClassName = navigationDestinationClassName,
            androidGraphClassName = androidGraphClassName,
        )
        val navigationGraph = graphType.createGraph(
            declaration = classDeclaration,
        )

        file.addType(
            navigationGraph.build()
        )

        file.build().writeTo(codeGenerator, Dependencies(true, sourceFile))
    }

    private fun getDestinationType(
        destinationsMap: Map<KSType, KSClassDeclaration?>,
        screen: KSClassDeclaration?
    ) = destinationsMap.filter {
        val ksType = it.key
        (ksType.ksClassDeclaration()).getAllSuperTypes().first().declaration == screen
    }.values.filterNotNull()
}
