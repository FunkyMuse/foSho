package dev.funkymuse.fosho.navigator.codegen.codegen

import dev.funkymuse.fosho.navigator.codegen.ClassNames
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.ANDROIDX_NAV_HOST_CONTROLLER
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.ANDROIDX_SAVED_STATE_HANDLE
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.NAMED_NAV_ARGUMENT
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.NAV_BACK_STACK_ENTRY
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Android.NAV_DEEP_LINK
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.COMPOSE_REMEMBER
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.animatedContentScope
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.columnScope
import dev.funkymuse.fosho.navigator.codegen.ClassNames.Compose.composable
import dev.funkymuse.fosho.navigator.codegen.ClassNames.WrappedNavigation.COMPOSED_NAV_ENTRY
import dev.funkymuse.fosho.navigator.codegen.ClassNames.WrappedNavigation.COMPOSED_VIEW_MODEL
import dev.funkymuse.fosho.navigator.codegen.ClassNames.WrappedNavigation.NavigationEntryArguments
import dev.funkymuse.fosho.navigator.codegen.ClassNames.WrappedNavigation.ViewModelNavigationArguments
import dev.funkymuse.fosho.navigator.codegen.Constants
import dev.funkymuse.fosho.navigator.codegen.UIType
import dev.funkymuse.fosho.navigator.codegen.annotation.Argument
import dev.funkymuse.fosho.navigator.codegen.annotation.CallbackArgument
import dev.funkymuse.fosho.navigator.codegen.argument.ArgumentType
import dev.funkymuse.fosho.navigator.codegen.deepLinksTemplate
import dev.funkymuse.fosho.navigator.codegen.findArgumentValue
import dev.funkymuse.fosho.navigator.codegen.getAnnotation
import dev.funkymuse.fosho.navigator.codegen.ksClassDeclaration
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import java.io.File
import java.util.Locale

internal fun androidGeneratedArgumentsProperty() = PropertySpec.builder(
    name = Constants.arguments,
    type = List::class.asClassName().parameterizedBy(NAMED_NAV_ARGUMENT),
    KModifier.OVERRIDE
)
    .getter(
        FunSpec.getterBuilder()
            .addCode(
                buildCodeBlock {
                    add("%L", "${Constants.`return`} ")
                    add(
                        "argumentsList.map{ toNavArgument(it) \n}"
                    )
                }
            )
            .build()
    )
    .build()

internal fun androidDestinationDeclaration(declaration: KSClassDeclaration) =
    "${declaration.qualifiedName?.getShortName()}${Constants.Destination}"


internal fun androidGeneratedDeepLinksProperty(screenClass: ClassName) = PropertySpec.builder(
    name = Constants.deepLinks,
    type = List::class.asClassName().parameterizedBy(NAV_DEEP_LINK),
    KModifier.OVERRIDE
)
    .getter(
        FunSpec.getterBuilder()
            .addCode(
                buildCodeBlock {
                    add(
                        "%L",
                        "${Constants.`return`} ${deepLinksTemplate(screenClass) + ".map{ toDeepLink(it) \n}"}"
                    )
                }
            )
            .build()
    )
    .build()

internal fun androidGeneratedRouteCompanionProperty(declaration: KSClassDeclaration) =
    PropertySpec.builder(
        name = Constants.route,
        type = String::class.asTypeName(),
        KModifier.CONST
    )
        .initializer(buildCodeBlock {
            add("%S", androidDestinationDeclaration(declaration))
        })
        .build()

internal fun androidGeneratedDestinationRoute() =
    PropertySpec.builder(name = Constants.destination, type = String::class, KModifier.OVERRIDE)
        .getter(
            FunSpec.getterBuilder()
                .addCode(
                    codeBlock = buildCodeBlock {
                        add("%L", "${Constants.`return`} ")
                        add(
                            "%L",
                            "toDestinationRoute("
                        )
                        add("%L", "${Constants.route},")
                        add("%L", Constants.argumentsList)
                        add("%L", ")")
                    }
                )
                .build()
        )
        .build()

internal data class AndroidGeneratedOpenFunction(
    val openFunSpec: FunSpec,
    val argumentListPropertySpec: PropertySpec,
    val viewModelClassSpec: TypeSpec?,
    val screenEntryClassSpec: TypeSpec?,
    val callbackArgumentsTypeSpec: TypeSpec?
)

internal fun androidGeneratedOpenFunction(
    screenClassName: String,
    declaration: KSClassDeclaration,
    generateViewModelArguments: Boolean,
    generateScreenEntryArguments: Boolean,
    defaultNavArgumentValue: KSClassDeclaration?
): AndroidGeneratedOpenFunction {
    val screenClass = screenClassName.substringAfterLast(".")
    val openClassFunSpec = FunSpec.builder("open$screenClass")

    val argumentSpec = argumentListSpec()
    val argumentListGetterBuilder = FunSpec.getterBuilder()

    openClassFunSpec.addCode(buildCodeBlock {
        add(
            "%L",
            "val ${Constants.arguments} = buildList<${Constants.ArgumentContract}> {"
        )
    })

    val argumentsAnnotation = declaration.getAnnotation(Argument::class).toList()
    argumentListGetterBuilder.addCode(buildCodeBlock {
        add("%L", "${Constants.`return`} ${Constants.listOf}(")
    })

    val shouldGenerateViewModelArguments =
        generateViewModelArguments && argumentsAnnotation.isNotEmpty()
    val shouldGenerateScreenEntryArguments =
        generateScreenEntryArguments && argumentsAnnotation.isNotEmpty()

    var viewModelClassSpec: TypeSpec.Builder? = null
    var screenEntryClassSpec: TypeSpec.Builder? = null
    var callbackArgumentsTypeSpec: TypeSpec.Builder? = null
    if (shouldGenerateViewModelArguments) {
        viewModelClassSpec = argumentTypeSpec(
            declaration = declaration,
            screenClass = screenClass, classSuffix = Constants.ViewModelArguments,
            superInterface = ViewModelNavigationArguments,
            defaultConstructorPropertyName = Constants.savedStateHandle,
            defaultConstructorPropertyType = ANDROIDX_SAVED_STATE_HANDLE
        )
    }

    if (shouldGenerateScreenEntryArguments) {
        screenEntryClassSpec = argumentTypeSpec(
            declaration = declaration,
            screenClass = screenClass,
            classSuffix = Constants.NavArguments,
            superInterface = NavigationEntryArguments,
            defaultConstructorPropertyName = Constants.currentNavBackStackEntry,
            defaultConstructorPropertyType = NAV_BACK_STACK_ENTRY
        )
    }

    val openFunctionParams = argumentsAnnotation.mapIndexed { index, argumentAnnotation ->
        val defaultValueClass: String?
        val name = argumentAnnotation.findArgumentValue<String>(Constants.name)
        val isNullable = argumentAnnotation.findArgumentValue<Boolean>(Constants.isNullable)
        val argumentEnumType = ArgumentType.valueOf(
            argumentAnnotation.findArgumentValue<KSType>(Constants.argumentType)
                .ksClassDeclaration().simpleName.asString()
        )
        val argumentType = argumentTypeSpec(argumentEnumType, isNullable)

        val parameterSpec = ParameterSpec.builder(name = name, type = argumentType)
        val defaultArgumentValue =
            argumentAnnotation.findArgumentValue<KSType>(Constants.defaultValue)

        if (defaultNavArgumentValue != defaultArgumentValue.ksClassDeclaration()) {
            defaultValueClass =
                "${
                    defaultArgumentValue.ksClassDeclaration().toClassName()
                }.${Constants.defaultValue}"
            parameterSpec.defaultValue(codeBlock = buildCodeBlock {
                add(
                    "%L", defaultValueClass
                )
            })

            argumentListGetterBuilder.addCode(buildCodeBlock {
                add("\n")
                add(
                    "%L",
                    "${Constants.ArgumentContract}("
                )
                add("\n")
                add("%L", "${Constants.name} = ")
                add("%S", name)
                add("%L", ",")
                add("\n")
                add(
                    "%L",
                    "${Constants.argumentType}=${Constants.ArgumentType}.$argumentEnumType,"
                )
                add("\n")
                add("%L", "${Constants.isNullable} = $isNullable,")
                add("\n")
                add(
                    "%L",
                    "${Constants.defaultValue} = $defaultValueClass) ${
                        argumentSeparator(
                            index,
                            argumentsAnnotation.lastIndex
                        )
                    }"
                )
                add("\n")
            })

        } else {
            defaultValueClass = null
            argumentListGetterBuilder.addCode(buildCodeBlock {
                add("\n")
                add(
                    "%L",
                    "${Constants.ArgumentContract}("
                )
                add("\n")
                add("%L", "${Constants.name} = ")
                add("%S", name)
                add("%L", ",")
                add("\n")
                add(
                    "%L",
                    "${Constants.argumentType}=${Constants.ArgumentType}.$argumentEnumType,"
                )
                add("\n")
                add(
                    "%L",
                    "${Constants.isNullable} = $isNullable) ${
                        argumentSeparator(
                            index,
                            argumentsAnnotation.lastIndex
                        )
                    }"
                )
                add("\n")
            })
        }

        openClassFunSpec.addCode(buildCodeBlock {
            add("\n")
            add(
                "%L",
                "add(${Constants.ArgumentContract}("
            )
            add("\n")
            add("%L", "${Constants.name} = ")
            add("%S", name)
            add("%L", ",")
            add("\n")
            add(
                "%L",
                "${Constants.argumentType}=${Constants.ArgumentType}.$argumentEnumType,"
            )
            add("\n")
            add("%L", "${Constants.isNullable} = $isNullable,")
            add("\n")
            add("%L", "${Constants.defaultValue} = $name))")
            add("\n")
        })

        viewModelClassSpec?.apply {
            addSafeArgType(
                name = name,
                argumentType = argumentType,
                defaultValueClass = defaultValueClass,
                argumentEnumType = argumentEnumType,
                isViewModel = true,
                isNullable = isNullable
            )
        }

        screenEntryClassSpec?.apply {
            addSafeArgType(
                name = name,
                argumentType = argumentType,
                defaultValueClass = defaultValueClass,
                argumentEnumType = argumentEnumType,
                isViewModel = false,
                isNullable = isNullable
            )
        }

        parameterSpec.build()
    }

    val screenEntryRememberHelper: FunSpec.Builder? = screenEntryRememberHelperArgs(
        shouldGenerateScreenEntryArguments,
        screenClass
    )

    val callbackArguments = declaration.getAnnotation(CallbackArgument::class)
    if (callbackArguments.toList().isNotEmpty()) {
        callbackArgumentsTypeSpec = TypeSpec.classBuilder(
            ClassName(
                declaration.packageName.asString(),
                "${screenClass}${Constants.CallbackArguments}"
            )
        )
            .addKdoc(generatedFromDocs(declaration))
            .addSuperinterface(ClassNames.CodegenAndroid.CallbackArguments)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder(
                            name = Constants.navHostController,
                            type = ANDROIDX_NAV_HOST_CONTROLLER,
                        )
                            .build()
                    )
                    .build()
            )
            .addProperty(
                PropertySpec.builder(
                    name = Constants.navHostController,
                    type = ANDROIDX_NAV_HOST_CONTROLLER,
                    KModifier.OVERRIDE
                ).initializer(Constants.navHostController)
                    .build()
            )
        val consumeArgumentsAtReceivingDestination =
            FunSpec.builder(Constants.consumeAllArgumentsAtReceivingDestination)
                .addModifiers(KModifier.OVERRIDE)
        val callbackArgumentsCompanionObjectTypeSpec = TypeSpec.companionObjectBuilder()
        val addCallbackResultTypeSpec = FunSpec.builder(Constants.addCallbackResult)
        val callbackParamsList = callbackArguments.map { argumentAnnotation ->
            val name = argumentAnnotation.findArgumentValue<String>(Constants.name)
            val nameConstParam = name.camelToSnakeCaseUpper()
            val isNullable = argumentAnnotation.findArgumentValue<Boolean>(Constants.isNullable)
            val argumentEnumType = ArgumentType.valueOf(
                argumentAnnotation.findArgumentValue<KSType>(Constants.argumentType)
                    .ksClassDeclaration().simpleName.asString()
            )
            val argumentType = argumentTypeSpec(argumentEnumType, isNullable)
            callbackArgumentsCompanionObjectTypeSpec.addProperty(
                PropertySpec.builder(
                    name = nameConstParam,
                    type = String::class.asTypeName(),
                    KModifier.CONST
                )
                    .initializer(buildCodeBlock {
                        add("%S", name)
                    })
                    .build()
            )

            val parameterSpec = ParameterSpec.builder(name = name, type = argumentType)
            val defaultArgumentValue =
                argumentAnnotation.findArgumentValue<KSType>(Constants.defaultValue)

            if (defaultNavArgumentValue != defaultArgumentValue.ksClassDeclaration()) {
                parameterSpec.defaultValue(codeBlock = buildCodeBlock {
                    add(
                        "%L",
                        "${
                            defaultArgumentValue.ksClassDeclaration().toClassName()
                        }.${Constants.defaultValue}"
                    )
                })
            }
            addCallbackResultTypeSpec.addCode(
                buildCodeBlock {
                    add("%L", "set($nameConstParam, $name)\n")
                }
            )

            consumeArgumentsAtReceivingDestination.addCode(
                buildCodeBlock {
                    add("%L", "${Constants.consumeArgument}($nameConstParam)\n")
                }
            )

            callbackArgumentsTypeSpec.addFunction(
                FunSpec.builder(
                    "consume${
                        name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }"
                )
                    .addCode(buildCodeBlock {
                        add("%L", "${Constants.consumeArgument}($nameConstParam)\n")
                    })
                    .build()
            )

            callbackArgumentsTypeSpec.addProperty(
                PropertySpec.builder(
                    "get${
                        name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }",
                    type = argumentType.copy(nullable = true),
                )
                    .getter(
                        FunSpec.getterBuilder()
                            .addCode(buildCodeBlock {
                                add(
                                    "%L",
                                    "${Constants.`return`} ${Constants.navHostController}.${Constants.currentEntry}.getResult<$argumentType?>("
                                )
                                add("%S", name)
                                add("%L", ")")
                            })
                            .build()
                    )
                    .build()
            )
            parameterSpec.build()
        }

        callbackArgumentsTypeSpec
            .addFunction(
                addCallbackResultTypeSpec.addParameters(callbackParamsList.toList()).build()
            )
            .addType(
                callbackArgumentsCompanionObjectTypeSpec.addFunction(
                    callbackRememberHelperArgs(
                        screenClass
                    ).build()
                ).build()
            )
            .addFunction(consumeArgumentsAtReceivingDestination.build())
    }


    return AndroidGeneratedOpenFunction(
        openFunSpec = openClassFunSpec
            .addParameters(openFunctionParams.toList())
            .addCode(codeBlock = buildCodeBlock {
                add("%L", "}")
                add("%L", "\n")
                add(
                    "${Constants.`return`} %L", "toOpenFunction(${Constants.arguments},"
                )
                add("%L", Constants.route)
                add("%L", ")")
            })
            .returns(String::class)
            .build(),
        argumentListPropertySpec = argumentSpec.getter(
            argumentListGetterBuilder
                .addCode(buildCodeBlock {
                    add("%L", ")")
                })
                .build()
        ).build(),
        viewModelClassSpec = viewModelClassSpec?.build(),
        screenEntryClassSpec = screenEntryClassSpec?.apply {
            screenEntryRememberHelper?.let { rememberHelper ->
                addType(
                    TypeSpec.companionObjectBuilder()
                        .addFunction(rememberHelper.build())
                        .build()
                )
            }
        }?.build(),
        callbackArgumentsTypeSpec = callbackArgumentsTypeSpec?.build()
    )
}

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
private fun String.camelToSnakeCaseUpper(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.uppercase()
}

private fun screenEntryRememberHelperArgs(
    shouldGenerateScreenEntryArguments: Boolean,
    screenClass: String,
): FunSpec.Builder? = if (shouldGenerateScreenEntryArguments) {
    val argsClassName = ClassName.bestGuess("${screenClass}${Constants.NavArguments}")
    FunSpec.builder(
        "${Constants.remember}${screenClass}${Constants.NavArguments}"
    )
        .addAnnotation(composable)
        .returns(argsClassName)
        .addCode(buildCodeBlock {
            add(
                "%L",
                " val ${Constants.navHostController} = ${Constants.navHostController}${Constants.OrThrow}\n"
            )
            add(
                "%L", " ${Constants.`return`} $COMPOSE_REMEMBER {\n" +
                        "        ${"${screenClass}${Constants.NavArguments}"}(${Constants.navHostController}.${Constants.currentEntry})\n" +
                        "    }"
            )
        })
} else {
    null
}

private fun callbackRememberHelperArgs(
    screenClass: String,
): FunSpec.Builder =
    FunSpec.builder(
        "${Constants.remember}${screenClass}${Constants.CallbackArguments}"
    )
        .addAnnotation(composable)
        .returns(ClassName.bestGuess("${screenClass}${Constants.CallbackArguments}"))
        .addCode(buildCodeBlock {
            add(
                "%L",
                " val ${Constants.navHostController} = ${Constants.navHostController}${Constants.OrThrow}\n"
            )
            add(
                "%L", " ${Constants.`return`} $COMPOSE_REMEMBER {\n" +
                        "        ${"${screenClass}${Constants.CallbackArguments}"}(${Constants.navHostController})\n" +
                        "    }"
            )
        })


private fun argumentTypeSpec(
    argumentEnumType: ArgumentType,
    isNullable: Boolean
): TypeName = when (argumentEnumType) {
    ArgumentType.INT -> Int::class.asClassName()
    ArgumentType.STRING -> String::class.asClassName()
    ArgumentType.BOOLEAN -> Boolean::class.asClassName()
    ArgumentType.FLOAT -> Float::class.asClassName()
    ArgumentType.LONG -> Long::class.asClassName()
    ArgumentType.BOOLEAN_ARRAY -> BooleanArray::class.asClassName()
    ArgumentType.FLOAT_ARRAY -> FloatArray::class.asClassName()
    ArgumentType.INT_ARRAY -> IntArray::class.asClassName()
    ArgumentType.LONG_ARRAY -> LongArray::class.asClassName()
    ArgumentType.STRING_ARRAY -> Array::class.asClassName()
        .parameterizedBy(String::class.asTypeName())

    ArgumentType.BOOLEAN_ARRAY_LIST -> List::class.parameterizedBy(Boolean::class)
    ArgumentType.FLOAT_ARRAY_LIST -> List::class.parameterizedBy(Float::class)
    ArgumentType.INT_ARRAY_LIST -> List::class.parameterizedBy(Int::class)
    ArgumentType.LONG_ARRAY_LIST -> List::class.parameterizedBy(Long::class)
    ArgumentType.STRING_ARRAY_LIST -> List::class.parameterizedBy(String::class)
    ArgumentType.URI -> ClassNames.Android.Uri
    ArgumentType.FILE -> File::class.asClassName()
}.copy(nullable = isNullable)

private fun argumentTypeSpec(
    declaration: KSClassDeclaration,
    screenClass: String,
    classSuffix: String,
    superInterface: ClassName,
    defaultConstructorPropertyName: String,
    defaultConstructorPropertyType: ClassName,
) = TypeSpec.classBuilder(
    ClassName(
        declaration.packageName.asString(),
        "${screenClass}$classSuffix"
    )
)
    .addKdoc(generatedFromDocs(declaration, classSuffix))
    .addSuperinterface(superInterface)
    .primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameter(
                ParameterSpec.builder(
                    name = defaultConstructorPropertyName,
                    type = defaultConstructorPropertyType
                ).build()
            )
            .build()
    ).addProperty(
        PropertySpec.builder(
            name = defaultConstructorPropertyName,
            type = defaultConstructorPropertyType,
            KModifier.OVERRIDE
        ).initializer(defaultConstructorPropertyName)
            .build()
    )

private fun TypeSpec.Builder.addSafeArgType(
    name: String,
    argumentType: TypeName,
    defaultValueClass: String?,
    argumentEnumType: ArgumentType,
    isViewModel: Boolean,
    isNullable: Boolean
) {
    val type = if (isViewModel) "$COMPOSED_VIEW_MODEL." else "$COMPOSED_NAV_ENTRY."
    addProperty(
        PropertySpec.builder(
            name = name, type = argumentType.copy(
                nullable = isNullable
            )
        )
            .initializer(buildCodeBlock {
                add(
                    "%L", when (argumentEnumType) {
                        ArgumentType.INT -> "${type}getIntArgument("
                        ArgumentType.STRING -> "${type}getStringArgument("
                        ArgumentType.BOOLEAN -> "${type}getBooleanArgument("
                        ArgumentType.FLOAT -> "${type}getFloatArgument("
                        ArgumentType.LONG -> "${type}getLongArgument("
                        ArgumentType.BOOLEAN_ARRAY -> "${type}getBooleanArrayArgument("
                        ArgumentType.FLOAT_ARRAY -> "${type}getFloatArrayArgument("
                        ArgumentType.INT_ARRAY -> "${type}getIntArrayArgument("
                        ArgumentType.LONG_ARRAY -> "${type}getLongArrayArgument("
                        ArgumentType.STRING_ARRAY -> "${type}getStringArrayArgument("
                        ArgumentType.BOOLEAN_ARRAY_LIST -> "${type}getBooleanArrayListArgument("
                        ArgumentType.FLOAT_ARRAY_LIST -> "${type}getFloatArrayListArgument("
                        ArgumentType.INT_ARRAY_LIST -> "${type}getIntArrayListArgument("
                        ArgumentType.LONG_ARRAY_LIST -> "${type}getLongArrayListArgument("
                        ArgumentType.STRING_ARRAY_LIST -> "${type}getStringArrayListArgument("
                        ArgumentType.URI -> "${type}getUriArgument("
                        ArgumentType.FILE -> "${type}getFileArgument("
                    }
                )
                add("%S", name)
                add("%L", ")")

                when {
                    isNullable -> {} //is nullable true and no default value class
                    defaultValueClass.isNullOrEmpty() -> {
                        add(
                            "%L",
                            "?: ${Constants.`throw`} ${Constants.IllegalArgumentException}("
                        )
                        add("%S", "$name is marked as non null but is null")
                        add("%L", ")")
                    }

                    !isNullable && defaultValueClass.isEmpty() -> {
                        add(
                            "%L",
                            "?: ${Constants.`throw`} ${Constants.IllegalArgumentException}("
                        )
                        add("%S", "$name is marked as non null but is null")
                        add("%L", ")")
                    }

                    !isNullable && defaultValueClass.isNotEmpty() -> {
                        add(
                            "%L", "?: $defaultValueClass"
                        )
                    }
                }
            })
            .build()
    )
}

private fun argumentSeparator(
    index: Int,
    lastIndex: Int,
) = if (index == lastIndex) "" else ","

private fun argumentListSpec() = PropertySpec.builder(
    name = Constants.argumentsList,
    type = List::class.asClassName().parameterizedBy(ClassNames.Codegen.ArgumentContract),
    KModifier.OVERRIDE
)


internal fun androidGeneratedRenderStubFunction(uiType: UIType) = FunSpec.builder(Constants.Content)
    .addModifiers(KModifier.OVERRIDE)
    .apply {
        when (uiType) {
            UIType.DIALOG -> {}
            UIType.BOTTOM_SHEET -> receiver(columnScope)
            UIType.SCREEN -> receiver(animatedContentScope)
        }
    }
    .addAnnotation(composable)
    .addCode(codeBlock = buildCodeBlock {
        add(
            "${Constants.`return`} %L", "TODO(\"STUB\")"
        )
    })
    .build()

internal fun generatedFromDocs(declaration: KSClassDeclaration, additionalMessage: String = "") =
    buildCodeBlock {
        add("Generated from [${declaration.packageName.asString()}.${declaration.qualifiedName?.getShortName()!!}] $additionalMessage")
    }