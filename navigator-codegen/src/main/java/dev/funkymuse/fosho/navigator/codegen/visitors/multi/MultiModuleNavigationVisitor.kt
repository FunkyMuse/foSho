package dev.funkymuse.fosho.navigator.codegen.visitors.multi

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dev.funkymuse.fosho.navigator.codegen.ClassNames
import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.ClassNames.WrappedNavigation.COMPOSED_NAVIGATION_PACKAGE
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.UIType
import dev.funkymuse.fosho.navigator.codegen.annotation.Destination
import dev.funkymuse.fosho.navigator.codegen.argument.DefaultArgumentValue
import dev.funkymuse.fosho.navigator.codegen.classMustBeInternalVisibility
import dev.funkymuse.fosho.navigator.codegen.codegen.generators.AndroidDestinationImplementationGenerator
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet
import dev.funkymuse.fosho.navigator.codegen.contract.Dialog
import dev.funkymuse.fosho.navigator.codegen.contract.Screen
import dev.funkymuse.fosho.navigator.codegen.findArgumentValue
import dev.funkymuse.fosho.navigator.codegen.getAnnotation
import dev.funkymuse.fosho.navigator.codegen.ksClassDeclaration


internal class MultiModuleNavigationVisitor(
    private val resolver: Resolver,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val screenNavigationDestinationClassName: ClassName,
    private val dialogNavigationDestinationClassName: ClassName,
    private val bottomSheetNavigationDestinationClassName: ClassName,
    private val injectViewModelArguments : Boolean
) : KSVisitorVoid() {

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: Unit
    ) {
        if (classDeclaration.classKind != ClassKind.OBJECT) {
            logger.error("Only object can be annotated with @${Destination::class.java.simpleName}", classDeclaration)
            return
        }
        classDeclaration.classMustBeInternalVisibility(logger)

        val destinations = classDeclaration.getAnnotation(Destination::class)
        val destination = destinations.first()

        val bottomSheet = resolver.getClassDeclarationByName<BottomSheet>()
        val dialog = resolver.getClassDeclarationByName<Dialog>()
        val screen = resolver.getClassDeclarationByName<Screen>()
        val defaultNavArgument = resolver.getClassDeclarationByName<DefaultArgumentValue>()

        val destinationType = classDeclaration.getAllSuperTypes().first().ksClassDeclaration()

        val file = FileSpec.builder(
            packageName = LOCAL_PATH,
            fileName = "${classDeclaration.qualifiedName?.getShortName()}${Constants.Destination}"
        )
        val generateViewModelArguments: Boolean =
            destination.findArgumentValue(Constants.generateViewModelArguments)
        val generateScreenEntryArguments: Boolean =
            destination.findArgumentValue(Constants.generateScreenEntryArguments)

        file.addImport(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString())
        file.addImport(COMPOSED_NAVIGATION_PACKAGE, ClassNames.WrappedNavigation.currentEntry)
        file.addImport(COMPOSED_NAVIGATION_PACKAGE, ClassNames.WrappedNavigation.getResult)
        file.addImport(ClassNames.Codegen.ARGUMENTABLE_PACKAGE, Constants.ArgumentContract)
        file.addImport(ClassNames.Codegen.ARGUMENTABLE_PACKAGE, Constants.ArgumentType)
        file.addType(
            AndroidDestinationImplementationGenerator(
                screenClass = classDeclaration.toClassName(),
                navigationDestinationClassName = when (destinationType) {
                    dialog -> dialogNavigationDestinationClassName
                    screen -> screenNavigationDestinationClassName
                    bottomSheet -> bottomSheetNavigationDestinationClassName
                    else -> throw IllegalArgumentException("$classDeclaration must implement either ${Screen::class.java.simpleName}, ${Dialog::class.java.simpleName} or ${BottomSheet::class.java.simpleName} ")
                },
                destinationType = destinationType
            ).createDestination(
                declaration = classDeclaration,
                generateViewModelArguments = generateViewModelArguments,
                generateScreenEntryArguments = generateScreenEntryArguments,
                file = file,
                dialogTemplate = dialog,
                defaultNavArgumentValue = defaultNavArgument,
                uiType = when (destinationType) {
                    dialog -> UIType.DIALOG
                    screen -> UIType.SCREEN
                    bottomSheet -> UIType.BOTTOM_SHEET
                    else-> throw IllegalArgumentException("$classDeclaration must implement either ${Screen::class.java.simpleName}, ${Dialog::class.java.simpleName} or ${BottomSheet::class.java.simpleName} ")
                },
                injectViewModelArguments = injectViewModelArguments
            ).build()
        )

        val sourceFile = requireNotNull(destination.containingFile) {
            "Unable to find sourceFile for ${classDeclaration.qualifiedName?.getShortName()}"
        }

        file.build().writeTo(codeGenerator, Dependencies(true, sourceFile))
    }
}


