package dev.funkymuse.fosho.navigator.codegen.visitors.multi

import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.annotation.Graph
import dev.funkymuse.fosho.navigator.codegen.classMustBeInternalVisibility
import dev.funkymuse.fosho.navigator.codegen.codegen.generators.MultiModuleGraphImplementationGenerator
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
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet
import dev.funkymuse.fosho.navigator.codegen.contract.Screen

internal class MultiModuleGraphVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val screenNavigationDestinationClassName: ClassName,
    private val dialogNavigationDestinationClassName: ClassName,
    private val bottomSheetNavigationDestinationClassName: ClassName,
    private val navigationDestinationClassName: ClassName,
    private val androidGraphClassName: ClassName,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
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
        val isRootGraph = graph.findArgumentValue<Boolean>(Constants.rootGraph)
        val startingDestinationDeclaration = startingDestination.ksClassDeclaration()

        val bottomSheet = resolver.getClassDeclarationByName<BottomSheet>()
        val dialog = resolver.getClassDeclarationByName<Dialog>()
        val screen = resolver.getClassDeclarationByName<Screen>()


        val destinationsWithoutStarting = graph.findArgumentValue<List<KSType>>(Constants.destinations)

        require(destinationsWithoutStarting.none { ksType -> ksType.ksClassDeclaration().classKind != ClassKind.OBJECT }) {
            "Screen destinations must be objects, not classes!"
        }

        require(destinationsWithoutStarting.none { ksType -> ksType == startingDestination }) {
            "Starting destination must not be added inside destinations"
        }


        val destinations = destinationsWithoutStarting + startingDestination

        val destinationTypes = destinations.map { ksType ->
            (ksType.ksClassDeclaration()).getAllSuperTypes().first().declaration to ksType
        }

        val file = FileSpec.builder(
            packageName = LOCAL_PATH,
            fileName = "${classDeclaration.qualifiedName?.getShortName()}${Constants.Stub}"
        )
        val sourceFile = requireNotNull(graph.containingFile) {
            "Unable to find sourceFile for ${classDeclaration.qualifiedName?.getShortName()}"
        }

        val graphType = MultiModuleGraphImplementationGenerator(
            screens =
            destinationTypes.mapNotNull { if (it.first == screen) (it.second.ksClassDeclaration()) else null },
            dialogs =
            destinationTypes.mapNotNull { if (it.first == dialog) (it.second.ksClassDeclaration()) else null },
            bottomSheets =
            destinationTypes.mapNotNull { if (it.first == bottomSheet) (it.second.ksClassDeclaration()) else null },
            startingDestinationDeclaration = startingDestinationDeclaration,
            dialogType = dialogNavigationDestinationClassName,
            screenType = screenNavigationDestinationClassName,
            bottomSheetType = bottomSheetNavigationDestinationClassName,
            navigationDestinationClassName = navigationDestinationClassName,
            androidGraphClassName = androidGraphClassName,
        )
        val aggregatorGraph = graphType.createAggregatorGraph(
            declaration = classDeclaration,
            isRootGraph = isRootGraph
        )

        file.addType(
            aggregatorGraph.build()
        )

        file.build().writeTo(codeGenerator, Dependencies(true, sourceFile))
    }
}
