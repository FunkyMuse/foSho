package dev.funkymuse.fosho.navigator.codegen.codegen.generators

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import dev.funkymuse.fosho.navigator.codegen.ClassNames
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.dialogProperties
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.UIType
import dev.funkymuse.fosho.navigator.codegen.codegen.androidDestinationDeclaration
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedArgumentsProperty
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedDeepLinksProperty
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedDestinationRoute
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedOpenFunction
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedRenderStubFunction
import dev.funkymuse.fosho.navigator.codegen.codegen.androidGeneratedRouteCompanionProperty
import dev.funkymuse.fosho.navigator.codegen.codegen.generatedFromDocs

internal class AndroidDestinationImplementationGenerator(
    private val screenClass: ClassName,
    private val navigationDestinationClassName: ClassName,
    private val destinationType: KSClassDeclaration,
) {
    fun createDestination(
        declaration: KSClassDeclaration,
        generateViewModelArguments: Boolean,
        generateScreenEntryArguments: Boolean,
        file: FileSpec.Builder,
        dialogTemplate: KSClassDeclaration?,
        defaultNavArgumentValue: KSClassDeclaration?,
        uiType: UIType
    ): TypeSpec.Builder {
        val openAndArguments = androidGeneratedOpenFunction(
            screenClassName = screenClass.toString(),
            declaration = declaration,
            generateViewModelArguments = generateViewModelArguments,
            generateScreenEntryArguments = generateScreenEntryArguments,
            defaultNavArgumentValue = defaultNavArgumentValue
        )

        return TypeSpec
            .interfaceBuilder(androidDestinationDeclaration(declaration))
            .addSuperinterface(navigationDestinationClassName)
            .addKdoc(generatedFromDocs(declaration))
            .addProperty(openAndArguments.argumentListPropertySpec)
            .addProperty(androidGeneratedArgumentsProperty())
            .addProperty(androidGeneratedDeepLinksProperty(screenClass))
            .addProperty(androidGeneratedDestinationRoute())
            .addType(
                TypeSpec.companionObjectBuilder()
                    .addSuperinterface(ClassName.bestGuess(androidDestinationDeclaration(declaration)))
                    .addProperty(androidGeneratedRouteCompanionProperty(declaration))
                    .addFunction(openAndArguments.openFunSpec)
                    .addFunction(androidGeneratedRenderStubFunction(uiType))
                    .build()
            ).also {
                if (dialogTemplate == destinationType) {
                    it.addProperty(androidGeneratedDialogNavProperties())
                }
                openAndArguments.viewModelClassSpec?.apply {
                    file.addType(this)
                }
                openAndArguments.screenEntryClassSpec?.apply {
                    file.addType(this)
                }
                openAndArguments.callbackArgumentsTypeSpec?.apply {
                    file.addType(this)
                }
            }
    }

    private fun androidGeneratedDialogNavProperties() =
        PropertySpec.builder(
            dialogProperties,
            ClassNames.Compose.composeDialogProperties
        )
            .addModifiers(KModifier.OVERRIDE)
            .getter(
                FunSpec.getterBuilder()
                    .addCode(buildCodeBlock {
                        add(
                            "%L",
                            "${Constants.`return`} ${Constants.dialogPropertiesToNavProperties}(${screenClass.simpleName})"
                        )
                    })
                    .build()
            )
            .build()


}
