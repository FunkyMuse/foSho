package dev.funkymuse.fosho.navigator.codegen.provider

import dev.funkymuse.fosho.navigator.codegen.processor.NavigationProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class NavigationProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        NavigationProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
            isSingleModule = environment.options["foShoSingleModule"]?.toBooleanStrict() ?: false
        )
}