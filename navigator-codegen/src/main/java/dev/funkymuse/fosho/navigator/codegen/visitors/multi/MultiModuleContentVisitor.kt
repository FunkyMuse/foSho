package dev.funkymuse.fosho.navigator.codegen.visitors.multi

import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.ANDROIDX_NAVIGATION
import dev.funkymuse.fosho.navigator.codegen.ClassNames.CodegenAndroid.LOCAL_PATH
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.ANDROIDX_COMPOSE_ANIMATION
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.animatedContentScope
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.columnScope
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.composable
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.annotation.AggregatorContent
import dev.funkymuse.fosho.navigator.codegen.annotation.Content
import dev.funkymuse.fosho.navigator.codegen.classMustBeInternalVisibility
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
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dev.funkymuse.fosho.navigator.codegen.contract.BottomSheet
import dev.funkymuse.fosho.navigator.codegen.contract.DestinationContract
import dev.funkymuse.fosho.navigator.codegen.contract.Screen

internal class MultiModuleContentVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind != ClassKind.OBJECT) {
            logger.error(
                "Only object can be annotated with @${Content::class.java.simpleName}",
                classDeclaration
            )
            return
        }
        classDeclaration.classMustBeInternalVisibility(logger)

        val allContent = classDeclaration.getAnnotation(Content::class)
        val content = allContent.first()

        val file = FileSpec.builder(
            packageName = LOCAL_PATH,
            fileName = "${classDeclaration.qualifiedName?.getShortName()}${Constants.NavEntry}"
        )
        val sourceFile = requireNotNull(content.containingFile) {
            "Unable to find sourceFile for ${classDeclaration.qualifiedName?.getShortName()}"
        }

        val superTypes = classDeclaration.getAllSuperTypes().toList().firstOrNull()

        require(superTypes != null) {
            "${classDeclaration.toClassName()} ${mustImplementGeneratedDestinationInterfaceMessage()}"
        }

        val contentDeclaration =
            resolver.getClassDeclarationByName<DestinationContract>()?.asStarProjectedType()

        require(contentDeclaration?.isAssignableFrom(superTypes) == true) {
            "${classDeclaration.toClassName()} ${mustImplementGeneratedDestinationInterfaceMessage()}"
        }

        file.addImport(
            classDeclaration.packageName.asString(),
            classDeclaration.toClassName().simpleName
        )

        val bottomSheet = resolver.getClassDeclarationByName<BottomSheet>()
        val screen = resolver.getClassDeclarationByName<Screen>()!!

        val classSuperTypes = classDeclaration.getAllSuperTypes().map { it.ksClassDeclaration() }

        val isScreen = classSuperTypes.contains(screen)
        val isBottomSheet = classSuperTypes.contains(bottomSheet)
        if (isScreen) {
            file.addImport(ANDROIDX_COMPOSE_ANIMATION, Constants.AnimatedContentTransitionScope)
            file.addImport(ANDROIDX_COMPOSE_ANIMATION, Constants.EnterTransition)
            file.addImport(ANDROIDX_COMPOSE_ANIMATION, Constants.ExitTransition)
            file.addImport(ANDROIDX_COMPOSE_ANIMATION, Constants.AnimatedContentScope)
            file.addImport(ANDROIDX_NAVIGATION, Constants.NavBackStackEntry)
        }

        val currentContentClass = ClassName.bestGuess(classDeclaration.toClassName().simpleName)

        file.addType(
            TypeSpec.objectBuilder("${classDeclaration.qualifiedName?.getShortName()}${Constants.NavEntry}")
                .addAnnotation(
                    AnnotationSpec.builder(AggregatorContent::class)
                        .addMember(buildCodeBlock {
                            add("%L = ", Constants.forContent)
                            add("%T${Constants.SCOPED_CLASS}", classDeclaration.toClassName())
                        })
                        .build()
                )
                .addSuperinterface(
                    superTypes.toClassName()
                )
                .apply {
                    if (isScreen) {
                        addScreenAnimations(currentContentClass)
                    }
                }
                .addFunction(
                    FunSpec.builder(Constants.Content)
                        .addModifiers(KModifier.OVERRIDE)
                        .addAnnotation(composable)
                        .apply {
                            if (isScreen){
                                receiver(animatedContentScope)
                            }
                            if (isBottomSheet){
                                receiver(columnScope)
                            }
                        }
                        .addCode(codeBlock = buildCodeBlock {
                            add(
                                "${currentContentClass}.apply {\n"
                            )
                            add("\t${Constants.Content}() \n}")
                        })
                        .build()
                )
                .build()
        )

        file.build().writeTo(codeGenerator, Dependencies(true, sourceFile))
    }

    private fun mustImplementGeneratedDestinationInterfaceMessage() = "must implement the generated destination interface"

    private fun TypeSpec.Builder.addScreenAnimations(className: ClassName) {
        val enterTransitionLambda = getTransitionLambda(Constants.EnterTransition)
        val exitTransitionLambda = getTransitionLambda(Constants.ExitTransition)

        addTransition(enterTransitionLambda, className, Constants.enterTransition)
        addTransition(enterTransitionLambda, className, Constants.popEnterTransition)
        addTransition(exitTransitionLambda, className, Constants.exitTransition)
        addTransition(exitTransitionLambda, className, Constants.popExitTransition)
    }

    private fun TypeSpec.Builder.addTransition(
        enterTransitionLambda: TypeName,
        className: ClassName,
        transition: String
    ) {
        addProperty(
            PropertySpec.builder(
                transition,
                enterTransitionLambda,
                KModifier.OVERRIDE,
            )
                .getter(
                    FunSpec.getterBuilder()
                        .addCode("%L", "${Constants.`return`} $className.$transition")
                        .build()
                )
                .build()
        )
    }

    private fun getTransitionLambda(transition: String) = LambdaTypeName.get(
        receiver = ClassName(
            ANDROIDX_COMPOSE_ANIMATION,
            Constants.AnimatedContentTransitionScope
        ).parameterizedBy(
            ClassName(ANDROIDX_NAVIGATION, Constants.NavBackStackEntry)
        ),
        parameters = listOf(),
        returnType = ClassName(
            ANDROIDX_COMPOSE_ANIMATION,
            transition
        ).copy(nullable = true)
    ).copy(nullable = true)

}

